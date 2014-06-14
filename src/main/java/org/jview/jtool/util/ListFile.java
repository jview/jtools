package org.jview.jtool.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.sf.json.JSONObject;

/**
 * 
 * @author chenjinhe
 * 
 */
public class ListFile implements List {
	public static final String LIST_PATH = "d:/data/";
	public static final String LIST_FILE = "fileName";
	public static final int WRITE_CACHE_SIZE = 300;
	private String filePath = "d:/data/";
	private String fileName;
	private boolean sizeChange = true;// size是否变化,默认变化
	private boolean isString=false;
	private File opFile;
	private LineReader fileReader;
	private FileWriter fileWrite;
	private Class classType;
	private transient int size = 0;// 行数
	private String writeCache = null;// 缓存处理，在数据长度累积到一定量再写入
	private String path;

	/**
	 * 递归生成临时文件
	 * 
	 * @param count
	 * @return
	 */
	private File getOpFile(int count) {
		if (this.classType != null) {
			System.out.println(count);
			this.fileName = this.classType.getSimpleName();
		} else {
			this.fileName = LIST_FILE;
		}
		this.fileName = this.fileName + "_" + count + ".json";
		path = filePath + fileName;
		File file = new File(path);
		if (file.exists()) {
			return getOpFile(count + 1);
		}
		return file;
	}

	/**
	 * 准备读取
	 * 
	 * @return
	 * 
	 */
	private LineReader getFileReader() {
		if (fileReader == null) {
			FileReader fr;
			try {
				if (opFile == null) {
					this.opFile = this.getOpFile(0);
				}
				fr = new FileReader(opFile);
				fileReader = new LineReader(fr);
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			}
		}
		return fileReader;
	}

	/**
	 * 准备写入
	 * 
	 * @return
	 */
	private FileWriter getFileWrite() {
		if (fileWrite == null) {
			try {
				if (opFile == null) {
					this.opFile = this.getOpFile(0);
				}
				fileWrite = new FileWriter(this.opFile);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		return fileWrite;
	}

	/**
	 * 使用完关闭
	 */
	public void addEnd() {
		if (fileWrite != null) {
			try {
				if (writeCache != null) {
					fileWrite.write(writeCache);
					this.writeCache = null;
					this.sizeChange = true;
				}
				fileWrite.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		fileWrite = null;
	}

	/**
	 * 使用完关闭
	 */
	public void getEnd() {
		if (fileReader != null) {
			try {
				fileReader.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		fileReader = null;
	}

	public void setClass(Class c) {
		classType = c;
		this.isString=this.classType == String.class;
	}
	
	public void setOpFile(File opFile){
		this.opFile=opFile;
	}

	private String jsonStr;

	// 用于循环增加
	public boolean add(Object o) {
		fileWrite = this.getFileWrite();

		
		try {
			if (!this.isString) {
				jsonStr = JSONObject.fromObject(o).toString();
			} else {
				jsonStr = "" + o;
			}
			jsonStr = jsonStr + "\n";
			if (this.writeCache != null) {
				this.writeCache = this.writeCache + jsonStr;
			} else {
				this.writeCache = jsonStr;
			}
			if (this.writeCache.length() > WRITE_CACHE_SIZE) {
				fileWrite.write(writeCache);
				this.writeCache = null;
			}
			// fileWrite.write(jsonStr);
			sizeChange = true;
		} catch (IOException e) {
			
			e.printStackTrace();
			return false;
		}

		return true;
		
	}

	/**
	 * 插入行
	 */
	public void add(int index, Object element) {
		

		this.getEnd();
		fileReader = this.getFileReader();
		
		try {
			String line = null;
			File bakFile = File.createTempFile(fileName, ".tmp");
			FileWriter bakWrite = new FileWriter(bakFile);
			int count = 0;
			while ((line = this.fileReader.readLine()) != null) {
				count++;
				if (index == count) {
					if (this.classType != String.class) {
						jsonStr = JSONObject.fromObject(element).toString();
					} else {
						jsonStr = line;
					}
					jsonStr = jsonStr + "\n";
					bakWrite.write(jsonStr);
				}
				bakWrite.write(line + "\n");
			}
			bakWrite.close();
			this.getEnd();
			this.opFile.delete();
			boolean status = bakFile.renameTo(this.opFile);

			sizeChange = true;
		} catch (IOException e) {
			
			e.printStackTrace();
		}

		
	}

	/**
	 * 批量增加(追加方式)
	 */
	public boolean addAll(Collection c) {
		boolean status = false;
		if (c == null) {
			return status;
		}
		Object[] a = c.toArray();
		this.getEnd();
		fileReader = this.getFileReader();
		
		try {
			String line = null;
			File bakFile = File.createTempFile(fileName, ".tmp");
			// System.out.println(bakFile.getAbsolutePath());
			FileWriter bakWrite = new FileWriter(bakFile);
			int count = 0;
			while ((line = this.fileReader.readLine()) != null) {
				count++;
				bakWrite.write(line + "\n");
			}
			for (Object o : a) {
				if (this.classType != String.class) {
					jsonStr = JSONObject.fromObject(o).toString();
				} else {
					jsonStr = "" + o;
				}
				jsonStr = jsonStr + "\n";
				bakWrite.write(jsonStr);
			}
			bakWrite.close();
			this.getEnd();
			this.opFile.delete();
			status = bakFile.renameTo(this.opFile);
			// System.out.println("rename status="+status);

			sizeChange = true;
		} catch (IOException e) {
			
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * 批量插入
	 */
	public boolean addAll(int index, Collection c) {
		boolean status = false;
		if (c == null) {
			return status;
		}

		this.getEnd();
		Object[] a = c.toArray();
		fileReader = this.getFileReader();
		
		try {
			String line = null;
			File bakFile = File.createTempFile(fileName, ".tmp");
			FileWriter bakWrite = new FileWriter(bakFile);
			int count = 0;
			while ((line = this.fileReader.readLine()) != null) {
				count++;
				if (index == count) {
					for (Object o : a) {
						if (this.classType != String.class) {
							jsonStr = JSONObject.fromObject(o).toString();
						} else {
							jsonStr = "" + o;
						}
						bakWrite.write(jsonStr + "\n");
					}
				}
				bakWrite.write(line + "\n");
			}
			bakWrite.close();
			this.getEnd();
			this.opFile.delete();
			status = bakFile.renameTo(this.opFile);
			sizeChange = true;
		} catch (IOException e) {
			
			e.printStackTrace();
		}

		
		return status;
	}

	public void clear() {
		
		this.getEnd();
		this.addEnd();
		boolean isDelete = this.opFile.delete();
		System.out.println("isDelete=" + isDelete);
		this.fileReader = null;
		this.fileWrite = null;
		this.size = 0;
		this.sizeChange = true;
		this.fileName = null;
	}

	/**
	 * 是否包含对象o
	 */
	public boolean contains(Object o) {
		
		return indexOf(o) != -1;
	}

	/**
	 * @deprecated
	 */
	public boolean containsAll(Collection c) {
		
		return false;
	}

	public Object get(int index) {
		Object obj = null;
		fileReader = this.getFileReader();
		
		try {
			fileReader.setLineNumber(index);
			String str = fileReader.readLine();			
			if (this.classType != null&&!this.isString) {				
				obj = JSONObject.toBean(JSONObject.fromObject(str
							.trim()), classType);
			} else {
				obj = str;
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return obj;
		
	}

	/**
	 * 查找前面对象序号或行号
	 */
	public int indexOf(Object o) {
		
		int index = 0;
		String line = null;
		if (o != null) {
			this.getEnd();
			this.fileReader = this.getFileReader();
			if (this.classType != String.class) {
				jsonStr = JSONObject.fromObject(o).toString();
			} else {
				jsonStr = "" + o;
			}
			try {
				while ((line = fileReader.readLine()) != null) {
					if (jsonStr.equals(line)) {
						return index;
					}
					index++;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			this.getEnd();
		}
		return -1;
	}

	/**
	 * 是否为空
	 */
	public boolean isEmpty() {
		
		if (this.size() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * @deprecated
	 */
	public Iterator iterator() {
		
		return null;
	}

	/**
	 * 查找后面对象序号或行号
	 */
	public int lastIndexOf(Object o) {
		
		int index = 0;
		String line = null;
		if (o != null) {
			this.getEnd();
			this.fileReader = this.getFileReader();
			if (this.classType != String.class) {
				jsonStr = JSONObject.fromObject(o).toString();
			} else {
				jsonStr = "" + o;
			}
			try {
				int fIndex = -1;
				while ((line = fileReader.readLine()) != null) {
					if (jsonStr.equals(line)) {
						fIndex = index;
					}
					index++;
				}
				return fIndex;
			} catch (Exception e) {
				// TODO: handle exception
			}
			this.getEnd();
		}
		return -1;
	}

	/**
	 * @deprecated
	 */
	public ListIterator listIterator() {
		
		return null;
	}

	/**
	 * @deprecated
	 */
	public ListIterator listIterator(int index) {
		
		return null;
	}

	/**
	 * @deprecated
	 */
	public boolean remove(Object o) {
		
		int index = this.indexOf(o);

		return this.remove(index) != null;
	}

	/**
	 * @deprecated
	 */
	public Object remove(int index) {
		
		this.getEnd();
		Object result = null;
		fileReader = this.getFileReader();
		if (fileReader != null) {
			try {
				String line = null;
				File bakFile = File.createTempFile(fileName, ".tmp");
				FileWriter bakWrite = new FileWriter(bakFile);
				int count = 0;
				while ((line = this.fileReader.readLine()) != null) {
					count++;
					if (index != count) {

						bakWrite.write(line + "\n");
					} else {
						if (this.classType != String.class) {
							result = JSONObject.toBean(JSONObject
									.fromObject(line.trim()), classType);
						} else {
							result = line;
						}
					}

				}
				bakWrite.close();
				this.getEnd();
				this.opFile.delete();
				boolean status = bakFile.renameTo(this.opFile);

				sizeChange = true;
			} catch (IOException e) {
				
				e.printStackTrace();
			}

		}
		return result;
	}

	/**
	 * @deprecated
	 */
	public boolean removeAll(Collection c) {
		boolean isRemove = false;
		if (c == null) {
			return false;
		}
		Object[] a = c.toArray();
		this.getEnd();
//		Object result = null;
		fileReader = this.getFileReader();
		if (fileReader != null) {
			try {
				String line = null;
				File bakFile = File.createTempFile(fileName, ".tmp");
				FileWriter bakWrite = new FileWriter(bakFile);
				int count = 0;
				int removeCount = 0;
				while ((line = this.fileReader.readLine()) != null) {
					count++;
					isRemove = false;
					for (Object o : a) {
						if (this.classType != String.class) {
							jsonStr = JSONObject.fromObject(o).toString();
						} else {
							jsonStr = "" + o;
						}
						if (jsonStr.equals(line)) {
							isRemove = true;
						}
					}
					if (!isRemove) {
						removeCount++;
						bakWrite.write(line + "\n");
					}

				}
				bakWrite.close();
				this.getEnd();
				this.opFile.delete();
				boolean status = bakFile.renameTo(this.opFile);

				sizeChange = true;
			} catch (IOException e) {
				
				e.printStackTrace();
			}

		}
		return isRemove;
	}

	/**
	 * @deprecated
	 */
	public boolean retainAll(Collection c) {
		
		return false;
	}

	/**
	 * 行替换
	 */
	public Object set(int index, Object element) {
		
		this.getEnd();
		Object oldEle = null;
		fileReader = this.getFileReader();
		if (fileReader != null) {
			try {
				String line = null;
				File bakFile = File.createTempFile(fileName, ".tmp");
				FileWriter bakWrite = new FileWriter(bakFile);
				int count = 0;
				while ((line = this.fileReader.readLine()) != null) {
					count++;
					if (index == count) {
						if (this.classType != String.class) {
							oldEle = JSONObject.toBean(JSONObject
									.fromObject(line.trim()), classType);
							jsonStr = JSONObject.fromObject(element).toString()
									+ "\n";
						} else {
							jsonStr = line;
							oldEle = line;
						}
						bakWrite.write(jsonStr);
					} else {
						bakWrite.write(line + "\n");
					}
				}
				bakWrite.close();
				this.getEnd();
				this.opFile.delete();
				boolean status = bakFile.renameTo(this.opFile);

				sizeChange = true;
			} catch (IOException e) {
				
				e.printStackTrace();
			}

		}
		return oldEle;
	}

	/**
	 * 行数
	 */
	public int size() {
		
		try {
			int count = 0;
			if (sizeChange) {
				this.fileReader = this.getFileReader();
				if (this.fileReader != null)
					while (this.fileReader.readLine() != null) {
						count++;
					}
				sizeChange = false;
				this.size = count;
			}
		} catch (IOException e) {
			
			// e.printStackTrace();
		}
		return size;
	}

	/**
	 * 取得指定行
	 */
	public List subList(int fromIndex, int toIndex) {
		
		this.getEnd();
		List list = new ArrayList();
		this.fileReader = this.getFileReader();
		for (int i = fromIndex; i < toIndex; i++) {
			list.add(this.get(i));
		}
		this.getEnd();
		return list;
	}

	/**
	 * @deprecated
	 */
	public Object[] toArray() {
		
		return null;
	}

	/**
	 * @deprecated
	 */
	public Object[] toArray(Object[] a) {
		
		return null;
	}

}
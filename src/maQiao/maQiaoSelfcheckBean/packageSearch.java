/**
 * 
 */
package maQiao.maQiaoSelfcheckBean;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLDecoder;
import java.net.JarURLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 同包中查找含有接口的类
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class packageSearch {
	/**
	 * 得到对象同级的自检类
	 * @param obj
	 * @return
	 */
	static List<Class<?>> getAllClassByInterfaceSelf(Object obj) {
		List<Class<?>> classList = getAllClassByInterfaceAll(obj);
		Predicate<Class<?>> checkFilterSelfcheck = (p) -> (isClassSelfcheck(p, obj.getClass()));
		List<Class<?>> resultList = new ArrayList<Class<?>>();
		classList.stream().filter(checkFilterSelfcheck).forEach((p) -> {
			resultList.add(p);
		});
		return resultList;
	}

	/**
	 * 得到对象同级的自检类[所有自检类包括其它对象与类的]
	 * @param obj Object
	 * @return List&lt;Class&lt;?>>
	 */
	private static List<Class<?>> getAllClassByInterfaceAll(Object obj) {
		return getAllClassByInterface(obj.getClass().getPackage().getName(), InterfaceSC.class);
	}

	/**
	 * 判断某个类是否是自己类的自检类[先判断接口，再判断接口泛型是否指向自己类]
	 * @param classzz Class
	 * @param self Class
	 * @return boolean
	 */
	static final boolean isClassSelfcheck(Class<?> classzz, Class<?> self) {
		if (!InterfaceSC.class.isAssignableFrom(classzz)) return false;
		/* Filter 条件 */
		/*
		 * Predicate<Class<?>> classCheckFilter = (p) -> {
		 * Class<?> newClass = (Class<?>) p;
		 * if (newClass == self) return true;
		 * if (newClass.getName().equals(self.getName())) return true;
		 * return false;
		 * };
		 */
		Type[] typeArrays = classzz.getGenericInterfaces();
		for (Type type : typeArrays) {
			Type[] temp = ((ParameterizedType) type).getActualTypeArguments();
			for (Type c : temp) {
				Class<?> newClass = (Class<?>) c;
				if (newClass == self) return true;
				if (newClass.getName().equals(self.getName())) return true;
			}
		}
		return false;
	}

	/**
	 * 得到某个包下的所有此接口的类
	 * @param packageName String
	 * @param Interface Class
	 * @return List<Class<?>>
	 */
	private static List<Class<?>> getAllClassByInterface(String packageName, Class<?> Interface) {
		List<Class<?>> classList = new ArrayList<Class<?>>();
		if (Interface.isInterface()) {
			List<Class<?>> allClass = getClasses(packageName);
			if (allClass != null) {
				classList = new ArrayList<Class<?>>();
				for (Class<?> classes : allClass)
					if (Interface.isAssignableFrom(classes)) if (!Interface.equals(classes)) classList.add(classes);
			}
		}
		return classList;
	}

	/*
	 * 取得某一类所在包的所有类名 不含迭代
	 */
	static String[] getPackageAllClassName(String classLocation, String packageName) {
		//将packageName分解  
		String[] packagePathSplit = packageName.split("[.]");
		String realClassLocation = classLocation;
		int packageLength = packagePathSplit.length;
		for (int i = 0; i < packageLength; i++)
			realClassLocation = realClassLocation + File.separator + packagePathSplit[i];
		File packeageDir = new File(realClassLocation);
		if (packeageDir.isDirectory()) return packeageDir.list();
		return null;
	}

	/**
	 * 从包package中获取所有的Class
	 * @param pack String
	 * @return List&lt;Class&lt;?>>
	 */
	private static List<Class<?>> getClasses(String packageName) {
		//第一个class类的集合  
		List<Class<?>> classes = new ArrayList<Class<?>>();
		//是否循环迭代  
		boolean recursive = true;
		//获取包的名字 并进行替换  
		String packageDirName = packageName.replace('.', '/');
		//定义一个枚举的集合 并进行循环来处理这个目录下的things  
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			//循环迭代下去  
			while (dirs.hasMoreElements()) {
				//获取下一个元素  
				URL url = dirs.nextElement();
				//得到协议的名称  
				String protocol = url.getProtocol();
				//如果是以文件的形式保存在服务器上  
				if ("file".equals(protocol)) {
					//获取包的物理路径  
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					//以文件的方式扫描整个包下的文件 并添加到集合中  
					findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
				} else if ("jar".equals(protocol)) {
					//如果是jar包文件   
					//定义一个JarFile  
					JarFile jar;
					try {
						//获取jar  
						jar = ((JarURLConnection) url.openConnection()).getJarFile();
						//从此jar包 得到一个枚举类  
						Enumeration<JarEntry> entries = jar.entries();
						//同样的进行循环迭代  
						while (entries.hasMoreElements()) {
							//获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件  
							JarEntry entry = entries.nextElement();
							String name = entry.getName();
							//如果是以/开头的  
							if (name.charAt(0) == '/') {
								//获取后面的字符串  
								name = name.substring(1);
							}
							//如果前半部分和定义的包名相同  
							if (name.startsWith(packageDirName)) {
								int idx = name.lastIndexOf('/');
								//如果以"/"结尾 是一个包  
								if (idx != -1) {
									//获取包名 把"/"替换成"."  
									packageName = name.substring(0, idx).replace('/', '.');
								}
								//如果可以迭代下去 并且是一个包  
								if ((idx != -1) || recursive) {
									//如果是一个.class文件 而且不是目录  
									if (name.endsWith(".class") && !entry.isDirectory()) {
										//去掉后面的".class" 获取真正的类名  
										String className = name.substring(packageName.length() + 1, name.length() - 6);
										try {
											//添加到classes  
											classes.add(Class.forName(packageName + '.' + className));
										} catch (ClassNotFoundException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return classes;
	}

	/**
	 * 以文件的形式来获取包下的所有Class
	 * @param packageName
	 * @param packagePath
	 * @param recursive
	 * @param classes
	 */
	private static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, List<Class<?>> classes) {
		//获取此包的目录 建立一个File  
		File dir = new File(packagePath);
		//如果不存在或者 也不是目录就直接返回  
		if (!dir.exists() || !dir.isDirectory()) { return; }
		//如果存在 就获取包下的所有文件 包括目录  
		File[] dirfiles = dir.listFiles(new FileFilter() {
			//自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)  
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});
		//循环所有文件  
		for (File file : dirfiles) {
			//如果是目录 则继续扫描  
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
			} else {
				//如果是java类文件 去掉后面的.class 只留下类名  
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					//添加到集合中去  
					classes.add(Class.forName(packageName + '.' + className));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

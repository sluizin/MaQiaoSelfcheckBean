/**
 * 
 */
package maQiao.maQiaoSelfcheckBean;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 对内部类要在定义内部时请使用static修饰class
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@SuppressWarnings("unchecked")
public final class selfCheck {
	/** 静态缓存 */
	private static final Map<Class<?>, List<InterfaceSC<Object>>> cacheClassMap = new HashMap<Class<?>, List<InterfaceSC<Object>>>();

	/**
	 * 对本对象进行自检，自检对中出现一个false，则返回false
	 * @param obj Object
	 * @return boolean
	 */
	public static final boolean isSafeBean(Object obj) {
		return isSafeBean(obj, false);
	}

	/**
	 * 对本对象进行自检，自检对中出现一个false，则返回false
	 * @param obj Object
	 * @param report boolean
	 * @return boolean
	 */
	public static final boolean isSafeBean(Object obj, boolean report) {
		return checkListSelfcheck(obj, getList(obj), report);
	}

	/**
	 * 针对本对象运行自检对象，如果出现false，则返回false
	 * @param obj Object
	 * @param list List&lt;InterFaceSC&lt;Object>>则返回false
	 * @param report boolean
	 * @return boolean
	 */
	private static boolean checkListSelfcheck(Object obj, List<InterfaceSC<Object>> list, boolean report) {
		Predicate<InterfaceSC<Object>> checkFilter = (p) -> (!p.Selfcheck(obj));
		long sort = list.stream().filter(checkFilter).count();
		if (report) {
			List<selfResult> resultList = new ArrayList<selfResult>();
			list.stream().forEach((p) -> {
				selfResult e = new selfResult();
				e.obj = p;
				e.result = p.Selfcheck(obj);
				e.fileName = p.getClass().getName();
				resultList.add(e);
			});
			System.out.println("-=-=-=-=-=-=-=-=-=-=");
			selfResult.print(resultList);
			System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
		}
		if (sort > 0L) return false;
		return true;
	}

	/**
	 * 通过本对象得到这个对象的自检对象列表
	 * @param obj Object
	 * @return List&lt;InterFaceSC&lt;Object>>
	 */
	private static List<InterfaceSC<Object>> getList(Object obj) {
		List<InterfaceSC<Object>> list = new ArrayList<InterfaceSC<Object>>();
		list = cacheClassMap.get(obj.getClass());
		if (list != null) return list;
		List<Class<?>> extClassList = packageSearch.getAllClassByInterfaceSelf(obj);
		list = classToInterFaceSCobjList(obj, extClassList);
		cacheClassMap.put(obj.getClass(), list);
		return list;
	}

	/**
	 * 以本对象类为标准，在本包内找到的类表，判断是否是本类的自检类<br/>
	 * 并检索出并转换成新的对象表<br/>
	 * 小试:Lambdas和Streams
	 * @param obj Object
	 * @param extClassList List&lt;?>
	 * @return List&lt;InterFaceSC&lt;Object>>
	 */
	private static final List<InterfaceSC<Object>> classToInterFaceSCobjList(final Object obj, List<Class<?>> extClassList) {
		List<InterfaceSC<Object>> list = new ArrayList<InterfaceSC<Object>>();
		Predicate<Class<?>> checkFilterSelfcheck = (p) -> (packageSearch.isClassSelfcheck(p, obj.getClass()));
		extClassList.stream().filter(checkFilterSelfcheck).forEach((p) -> {
			try {
				list.add((InterfaceSC<Object>) p.newInstance());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		return list;
	}

	/**
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static class selfResult {
		String fileName = "";
		Object obj = null;
		boolean result = false;

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("[result=");
			builder.append(result);
			builder.append("\tfileName=");
			builder.append(fileName);
			builder.append("\tobj=");
			builder.append(obj);
			builder.append("]");
			return builder.toString();
		}

		public static void print(List<selfResult> list) {
			list.forEach((p) -> {
				System.out.println("report:" + p.toString());
			});
		}
	}

	/** 静态缓存 */
	@Deprecated
	private static Map<String, InterfaceSC<Object>> cacheMap = new HashMap<String, InterfaceSC<Object>>();
	/** 类扩展的关键字 */
	@Deprecated
	static final String ACC_classExt = "SC";

	@Deprecated
	public static final boolean check(Object obj) {

		return checkClassExtend(obj);
	}

	/**
	 * 对对象进行类扩展验证<br/>
	 * 对内部类要在定义内部时请使用static修饰class<br/>
	 * 自检类与源类处在同一目录，类名最后加上"SC"，并且 implements InterFaceSC<原类>
	 * @param value Object
	 * @return boolean
	 */
	@Deprecated
	public static final boolean checkClassExtend(Object value) {
		if (value == null) return false;
		InterfaceSC<Object> objExtend = getClassExtend(value);
		if (objExtend == null) return false;
		return objExtend.Selfcheck(value);
	}

	/**
	 * 得到扩展对象，通过接口<br/>
	 * 先从静态缓存中提取，如果没有，则检索文件
	 * @param classzzfilename String
	 * @return ISCObject
	 */
	@Deprecated
	private static final InterfaceSC<Object> getClassExtend(Object value) {
		final String classzzfilename = value.getClass().getName();
		InterfaceSC<Object> objExtend = cacheMap.get(classzzfilename);

		//selfCheck.cacheMap.forEach((key,player) -> System.out.println("player:"+player + "; "));
		//cacheMap.forEach((key,player) -> System.out.println("key:"+key + "; "));

		if (objExtend != null) {

			Type[] arr = value.getClass().getGenericInterfaces();
			for (int i = 0; i < arr.length; i++)
				System.out.println("arr[" + i + "]:" + arr[i].toString());
			Class<?>[] arr1 = value.getClass().getInterfaces();
			for (int i = 0; i < arr1.length; i++)
				System.out.println("arr1[" + i + "]:" + arr1[i].toString());
			Class<?>[] arr2 = value.getClass().getInterfaces();
			for (int i = 0; i < arr2.length; i++)
				System.out.println("arr2[" + i + "]:" + arr2[i].toString());

			Type type1 = value.getClass().getGenericSuperclass();
			Type type = objExtend.getClass().getGenericSuperclass();
			if (type != null) {
				System.out.println("type1:" + type1.toString());
				System.out.println("type:" + type.toString());
			}
		}
		if (objExtend != null) return objExtend;
		try {
			final String objExtendfileName = classzzfilename + ACC_classExt;
			String objExtendclassPath = value.getClass().getResource("/").getFile() + objExtendfileName.replace('.', '/') + ".class";
			System.out.println("objExtendclassPath:" + objExtendclassPath);
			File file = new File(objExtendclassPath);
			if (!file.isFile()) return null;
			Object obj = Class.forName(objExtendfileName).newInstance();
			if (obj instanceof InterfaceSC) {
				objExtend = (InterfaceSC<Object>) obj;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (objExtend != null) cacheMap.put(classzzfilename, objExtend);
		return objExtend;
	}

}

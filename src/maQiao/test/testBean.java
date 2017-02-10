/**
 * 
 */
package maQiao.test;

import maQiao.maQiaoSelfcheckBean.InterfaceSC;
import maQiao.maQiaoSelfcheckBean.selfCheck;

import org.junit.Test;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class testBean {

	@Test
	public void test() {
		/* 自检类不允许[value] {null "" "Boring" "very Boring"} */
		beanClass bean = new beanClass();
		bean.perName = "sunjian";
		String[] valueKeys = { null, "", "Boring", "Not boring", "very Boring", "very Happy" };
		boolean t;
		for (int i = 0, len = valueKeys.length; i < len; i++) {
			String key = valueKeys[i];
			bean.value = key;
			t = selfCheck.isSafeBean(bean,true);
			System.out.println("selfCheck[value]\tresult:" + t + "\tkey:\t" + ((key == null) ? "null" : "\"" + key + "\"")+"\t" + bean.toString());
		}
		/* 自检类不允许[perName]  {"Foolish"} */
		String[] perNameKeys = { null, "", "sunjian","Foolish" };
		for (int i = 0, len = perNameKeys.length; i < len; i++) 
		{
			String key = perNameKeys[i];
			bean.perName = key;
			t = selfCheck.isSafeBean(bean);
			System.out.println("selfCheck[perName]\tresult:" + t + "\tkey:\t" + ((key == null) ? "null" : "\"" + key + "\"")+"\t" + bean.toString());
		}
	}

	public class beanClass {
		String perName = "";
		String value = null;

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("[perName=");
			builder.append(changeString(perName));
			builder.append(", value=");
			builder.append(changeString(value));
			builder.append("]");
			return builder.toString();
		}

		String changeString(String key) {
			if (key == null) return "null";
			return "\"" + key + "\"";
		}
	}

	/**
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static class beanClassSC implements InterfaceSC<beanClass> {
		/*
		 * (non-Javadoc)
		 * @see maQiao.maQiaoSelfcheckBean.interFace.ISCObject#Selfcheck(java.lang.Object)
		 */
		public boolean Selfcheck(beanClass value) {
			if (value == null) return false;
			if (value.value == null) return false;
			if (value.value.length() == 0) return false;
			if (value.value.equals("Boring")) return false;
			return true;
		}
	}

	/**
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static class beanClassSC2 implements InterfaceSC<beanClass> {
		/*
		 * (non-Javadoc)
		 * @see maQiao.maQiaoSelfcheckBean.interFace.ISCObject#Selfcheck(java.lang.Object)
		 */
		public boolean Selfcheck(beanClass value) {
			if (value == null) return false;
			if (value.value == null) return false;
			if (value.value.equals("very Boring")) return false;
			return true;
		}
	}

	/**
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static class beanClassSCpersonName implements InterfaceSC<beanClass> {
		/*
		 * (non-Javadoc)
		 * @see maQiao.maQiaoSelfcheckBean.interFace.ISCObject#Selfcheck(java.lang.Object)
		 */
		public boolean Selfcheck(beanClass value) {
			if (value == null) return false;
			if (value.perName == null) return false;
			if (value.perName.length() == 0) return false;
			if (value.perName.equals("Foolish")) return false;
			return true;
		}
	}

	/**
	 * 其它自检类
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static class beanClassOtherSC implements InterfaceSC<beanClassSC> {
		public boolean Selfcheck(beanClassSC value) {
			if (value == null) return false;
			return true;
		}

	}
}

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
 * @since jdk1.7
 */
public class TestPoJo {

	@Test
	public void test() {
		PojoBean pojoBean = new PojoBean();
		System.out.println("idSafe:" + pojoBean.idSafe());
		pojoBean.name = "sunjian";
		pojoBean.score = 100;
		System.out.println("idSafe:" + pojoBean.idSafe());
	}
	/**
	 * PoJo Bean
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.78
	 */
	public static class PojoBean {
		int id;
		String name;
		int score;

		public boolean idSafe() {
			return selfCheck.isSafeBean(this);
		}
	}

	/**
	 * 对Bean进行自检
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static class PojoBeanSC implements InterfaceSC<PojoBean> {
		public boolean Selfcheck(PojoBean value) {
			if (value == null) return false;
			if (value.name == null || value.name.length() == 0) return false;
			if (value.score <= 0) return false;
			return true;
		}

	}
}

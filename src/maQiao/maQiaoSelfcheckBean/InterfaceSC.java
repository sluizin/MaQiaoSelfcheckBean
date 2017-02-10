/**
 * 
 */
package maQiao.maQiaoSelfcheckBean;

/**
 * 接口，T只针对原Bean<br/>
 * 因为使用了Lambda，所以只支持jdk1.8<br/>
 * 允许重写接口方法:public boolean Selfcheck(T value){}
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public interface InterfaceSC<T> {
	/**
	 * 对 T 进行自检
	 * @param value Object
	 * @return boolean
	 */
	public default boolean Selfcheck(T value) {
		if (value == null) return false;
		return true;
	}
}

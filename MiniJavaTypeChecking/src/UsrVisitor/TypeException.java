package UsrVisitor;

public class TypeException extends  Throwable {    //或者继承任何标准异常类
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public TypeException()  {}                //用来创建无参数对象
    public TypeException(String message) {        //用来创建指定参数对象
        super(message);                             //调用超类构造器
    }
}

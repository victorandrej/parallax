package parallax.log;

/**
 * representation of abstract logger
 * 
 * @author victor
 *
 */
public interface Log {
	public void push(LogType type, String message);
}

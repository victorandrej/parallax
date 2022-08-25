package parallax.log;

public class DefaultLogImplementation implements Log {

	@Override
	public void push(LogType type, String message) {
		switch (type) {
		case CRITICAL:
			System.err.println(type.name() + ": " + message);
			break;
		default:
			System.out.println(type.name() + ": " + message);
		}
	}
}

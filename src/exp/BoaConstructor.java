package exp;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface BoaConstructor {
	String[] fields();

}

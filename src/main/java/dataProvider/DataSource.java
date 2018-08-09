package dataProvider;

/**
 * Created by chenpei on 2018-07-30.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})

public @interface DataSource {

    String fileName() default "data.xlsx";

    String sheetName() default "Demo";
}

package example.item15other;

//import example.item15.PackagePrivateClass;  // import 안됨. Cannot be accessed from outside package
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import example.item15.PublicClass;
import org.junit.jupiter.api.Test;

class CapsuleTest {

  @Test
  void 다른_패키지에서_PackagePrivate클래스를_생성할_수_없다() {
//    PackagePrivateClass clazz = new PackagePrivateClass();  // Cannot be accessed from outside package
  }

  @Test
  void 다른_패키지에서_Public클래스를_생성할_수_있다() {
    assertThatCode(() -> {
      // when
      PublicClass clazz = PublicClass.of(0, 0, 0, 0);
    }).doesNotThrowAnyException();  // then
  }
}

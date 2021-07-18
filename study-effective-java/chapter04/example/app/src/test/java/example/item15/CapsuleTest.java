package example.item15;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

class CapsuleTest {

  @Test
  void 같은_패키지에서_PackagePrivate클래스를_생성할_수_있다() {
    assertThatCode(() -> {
      // when
      PackagePrivateClass clazz = new PackagePrivateClass();
    }).doesNotThrowAnyException();  // then
  }

  @Test
  void 같은_패키지에서_Public클래스를_생성할_수_있다() {
    assertThatCode(() -> {
      // when
      PublicClass clazz = PublicClass.of(0, 0, 0, 0);
    }).doesNotThrowAnyException();  // then
  }

  @Test
  void 같은_패키지에서_private멤버변수를_접근할_수_있다() {
    // given, when
    PublicClass clazz = PublicClass.of(0, 0, 0, 0);

    // then
    assertThat(clazz.getPrivateValue()).isZero();
  }

  @Test
  void 같은_패키지에서_packagePrivate멤버변수를_접근할_수_있다() {
    // given, when
    PublicClass clazz = PublicClass.of(0, 0, 0, 0);

    // then
    assertThat(clazz.getPackagePrivateValue()).isZero();
  }

  @Test
  void 같은_패키지에서_protected멤버변수를_접근할_수_있다() {
    // given, when
    PublicClass clazz = PublicClass.of(0, 0, 0, 0);

    // then
    assertThat(clazz.getProtectedValue()).isZero();
  }

  @Test
  void 같은_패키지에서_public멤버변수를_접근할_수_있다() {
    // given, when
    PublicClass clazz = PublicClass.of(0, 0, 0, 0);

    // then
    assertThat(clazz.getPublicValue()).isZero();
  }
}

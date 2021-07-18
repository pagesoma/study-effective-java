package example.item15;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class PublicClass {

  private int privateValue;
  int packagePrivateValue;
  protected int protectedValue;
  public int publicValue;

  private PublicClass(
      int privateValue, int packagePrivateValue, int protectedValue, int publicValue) {
    this.privateValue = privateValue;
    this.packagePrivateValue = packagePrivateValue;
    this.protectedValue = protectedValue;
    this.publicValue = publicValue;

    log.info("PublicClass 생성자 호출");
  }

  public static PublicClass of(
      int privateValue, int packagePrivateValue, int protectedValue, int publicValue) {

    return new PublicClass(privateValue, packagePrivateValue, protectedValue, publicValue);
  }
}

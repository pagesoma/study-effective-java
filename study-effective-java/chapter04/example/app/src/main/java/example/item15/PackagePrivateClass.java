package example.item15;


import lombok.extern.slf4j.Slf4j;

@Slf4j
class PackagePrivateClass {

  PackagePrivateClass() {
    log.info("PackagePrivateClass 생성자 호출");
  }
}

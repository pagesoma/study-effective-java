# 2장. 객체 생성과 파괴
## 다루는 것
- `객체`를 만들어야 할 때와 만들지 말아야 할 때를 구분하는 법
- 올바른 객체 `생성` 방법과 불필요한 생성을 피하는 방법
- 제때 `파괴`됨을 보장하고 파괴 전에 수행해야 할 `정리 작업`을 관리하는 요령

## 아이템 목록
1. 생성자 대신 `정적 팩터리 메서드`를 고려하라
2. 생성자에 매개변수가 많다면 `빌더`를 고려하라
3. `생성자나 열거 타입`으로 `싱글턴`임을 보증하라
4. 인스턴스화를 막으려거든 `private 생성자`를 사용하라
5. 자원을 직접 명시하지 말고 `의존 객체 주입`을 사용하라
6. 불필요한 객체 생성을 피하라
7. 다 쓴 객체 `참조를 해제`하라
8. finalizer와 clener 사용을 피하라
9. try-finally보다는 `try-with-resources`를 사용하라

## 아이템 1. 생성자 대신 정적 팩터리 메서드를 고려하라
- 생성자보다 `정적 팩터리`를 사용하는 게 유리한 경우가 더 많으므로 무작정 public 생성자를 제공하던 습관이 있다면 고치자.
```java
// 정적 팩터리 메서드 구현 예시
public static Boolean valueOf(boolean b) {
 return b ? Boolean.TRUE : Boolean.FALSE; 
}
```

### 정적 팩터리 메서드가 생성자보다 좋은 장점 다섯가지
1. `이름`을 가질 수 있다. - BigInteger.probablePrime
2. 호출될 때마다 `인스턴스`를 `새로 생성하지는 않아도`` 된다. - Boolean.valueOf(boolean)
3. 반환 타입의 `하위 타입 객체`를 반환할 수 있는 능력이 있다. (유연성, 반환 타입으로 인터페이스 사용가능)
4. 입력 매개변수에 따라 `매번 다른 클래스의 객체`를 반환할 수 있다. - EnumSet, RegularEnumSet, JumboEnumSet
5. 정적 팩터리 메서드를 작성하는 시점에는 `반환할 객체의 클래스`가 존재하지 않아도 된다. - JDBC

### 단점
1. 상속을 하려면 public이나 protected 생성자가 필요하니 정적 팩터리 메서드만 제공하면 하위 클래스를 만들 수 없다.
    - 상속보다 `컴포지션`을 사용하도록 유도, 오히려 장점일 수 있다.
2. 정적 팩터리 메서드는 이름이 다양하므로 프로그래머가 찾기 어렵다.
    - 생성자처럼 API 설명에 명확히 드러나지 않음
   
## 아이템 2. 생성자에 매개변수가 많다면 빌더를 고려하라
- 생성자나 정적 팩터리가 처리해야 할 매개변수가 많다면 빌더 패턴을 선택하는 게 더 낫다.  
- 매개변수 중 다수가 필수가 아니거나 같은 타입이면 특히 더 그렇다.
```java
// 빌더패턴 사용한 생성자 호출 예시
NutritionFacts cocaCola = new NutritionFacts.Builder(240, 8)
        .calories(100).sodium(35).carbohydrate(27).build();
```
- `점층적 생성자 패턴` - 확장하기 어렵다!
- `자바빈즈 패턴`(세터 메서드들 호출) - 일관성 깨짐, 불변성 만들수 없음. 쓰레드 안전성 요구 시 추가작업 필요
- `빌더 패턴` - 계층적으로 설계된 클래스와 함께 쓰기에 좋다.

## 아이템 3. 생성자나 열거 타입으로 싱글턴임을 보증하라
- public 필드 방식과 비슷하지만, 더 간결하고 추가 노력 없이 `직렬화`할 수 있다.  
- `리플렉션` 공격에서도 제2의 인스턴스가 생기는 일을 완벽히 막아준다.   
- 단, 만들려는 싱글턴이 `Enum` 외의 클래스를 상속해야 한다면 이 방법은 사용할 수 없다.
```java
// 열거 타입 방식의 싱글턴 - 바람직한 방법
public enum Elvis {
  INSTANCE;
  
  public void leaveTheBuilding() { ... }
}
```

## 아이템 4. 인스턴스화를 막으려거든 private 생성자를 사용하라
- 이 방식은 `상속`을 불가능하게 하는 효과도 있다.  
- `추상 클래스`로 만드는 것으로는 `인스턴스화`를 막을 수 없다. `하위 클래스`로 인스턴스화 가능
```java
// 인스턴스를 만들 수 없는 유틸리티 클래스
public class UtilityClass {
  // 기본 생성자가 만들어지는 것을 막는다(인스턴스화 방지용).
  private UtilityClass() {
    throw new AssertionError();
  }
  ...
}
```

## 아이템 5. 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라
- `클래스`가 내부적으로 하나 이상의 `자원`에 의존하고, 그 자원이 클래스 `동작`에 영향을 준다면 `싱글턴`과 `정적 유틸리티 클래스`는 사용하지 않는 것이 좋다.   
- 이 기법은 클래스의 `유연성`, `재사용성`, `테스트 용이성`을 개선해준다.   
- `의존 객체 주입`은 `생성자`, `정적 팩터리`, `빌더` 모두에 똑같이 응용할 수 있다.   
- `불변을 보장`하여 (같은 자원을 사용하려는) 여러 클라이언트가 의존 객체들을 안심하고 공유할 수 있다.  
- 이 패턴의 쓸만한 변형으로 `팩터리 메서드 패턴`이 있다.
```java
// 의존 객체 주입은 유연성과 테스트 용이성을 높여준다.
public class SpellChecker {
  private final Lexicon dictionary;
  
  public SpellChecker(Lexicon dictionary) {
    this.dictionary = Objects.requireNonNull(dictionary);
  }
  
  public boolean isValid(String word) { ... }
  public List<String> suggestions(String typo) { ... }
}
```

## 아이템 6. 불필요한 객체 생성을 피하라
- `똑같은 기능`의 객체를 매번 생성하기보다는 객체 하나를 `재사용`하는 편이 나을 때가 많다.  
- 재사용은 빠르고 세련되다. 특히 `불변 객체`는 언제든 `재사용`할 수 있다.  
- 불변 객체만이 아니라 `가변 객체`라 해도 사용 중에 변경되지 않을 것임을 안다면 `재사용`할 수 있다.
```java
// 따라 하지 말 것! 실행될 때마다 String 인스턴스를 새로 만든다.
String s = new String("bikini");
// 개선된 버전
String s = "bikini";

// 성능이 중요한 상황에서 반복해 사용하기엔 적합하지 않은 로직
static boolean isRomanNumeral(String s) {
  return s.match("^(?=.)$");
}
// 값비싼 객체를 재사용해 성능을 개선한다.
public class RonamNumerals {
  private static final Pattern ROMAN = Pattern.compile("^(?=.)$");
  static boolean isRomanNumeral(String s) { 
    return s.match(ROMAN); 
  }
}
```

## 아이템 7. 다 쓴 객체 참조를 해제하라
- 일반적으로 자기 메모리를 직접 관리하는 클래스라면 프로그래머는 항시 `메모리 누수`에 주의해야한다.  
- `캐시` 역시 메모리 누수를 일으키는 `주범`이다.  
- `리스너(listener)` 혹은 `콜백(callback)`도 명확히 해지하지 않는다면 메모리 누수가 일어날 수 있다.  
- 메모리 누수는 철저한 `코드 리뷰`나 `힙 프로파일러` 같은 디버깅 도구를 동원해야만 발견되기도 하니 `예방법`을 익혀두는 것이 매우 중요하다.
```java
// 예제 Stack 클래스에 제대로 구현한 pop 메서드
public Object pop() {
  if (size == 0) throw new EmptyStackException();
  Object result = elements[--size];
  elements[size] = null;  // 다 쓴 참조 해제
  return result;
}
```

## 아이템 8. finalizer와 cleaner 사용을 피하라
- `finalizer`와 `cleaner`는 즉시 수행된다는 보장이 없어 제때 실행되어야 하는 작업은 절대 할 수 없다.  
- 얼마나 신속히 수행할지는 전적으로 가비지 컬렉터 알고리즘에 달렸으며, `GC` 구현마다 천차만별이다.  
- 심각한 `성능 문제`도 동반하고 finalizer 공격에 노출되어 심각한 `보안 문제`를 일으킬 수도 있다.  

## 아이템 9. try-finally보다는 try-with-resources를 사용하라
- 꼭 회수해야 하는 자원을 다룰 때는 `try-with-resources`를 사용하자.  
- `예외는 없다.` 코드는 더 짧고 분명해지고, 만들어지는 예외 정보도 훨씬 유용하다.
```java
// 복수의 자원을 처리하는 try-with-resources - 짧고 매혹적이다!
static void copy(String src, String dst) throws IOException {
  try (InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst)) {
    byte[] buf = new byte[BUFFER_SIZE];
    int n;
    while ((n = in.read(buf)) >= 0)
      out.write(buf, 0, n);
  }
}

// try-with-resources를 catch 절과 함께 쓰는 모습
static void firstLineOfFile(String path, String defaultVal) {
  try (BufferedReader br = new BufferedReader(new FileReader(path))) {
    return br.readLine();
  } catch (IOException e) {
    return defaultVal;
  }
```

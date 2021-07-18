# 4장. 클래스와 인터페이스
## 다루는 것
- 클래스와 인터페이스를 쓰기 편하고, 견고하며, 유연하게 만드는 방법

## 아이템 목록
15. 클래스와 멤버의 접근 권한을 최소화하라
16. public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라
17. 변경 가능성을 최소화하라
18. 상속보다는 컴포지션을 사용하라
19. 상속을 고려해 설계하고 문서화하라. 그러지 않았다면 상속을 금지하라
20. 추상 클래스보다는 인터페이스를 우선하라
21. 인터페이스는 구현하는 쪽을 생각해 설계하라
22. 인터페이스는 타입을 정의하는 용도로만 사용하라
23. 태그 달린 클래스보다는 클래스 계층구조를 활용하라
24. 멤버 클래스는 되도록 static으로 만들라
25. 톱레벨 클래스는 한 파일에 하나만 담으라

## 아이템 15. 클래스와 멤버의 접근 권한을 최소화하라
> 프로그램 요소의 접근성은 가능한 한 최소한으로 하고, 꼭 필요한 것만 골라 Public API로 설계하라.  
> 그 외에는 클래스, 인터페이스, 멤버가 의도치 않게 API로 공개되는 일이 없도록 해야 한다.

### 정보 은닉(캡슐화)의 장점
- 시스템 개발 속도를 높인다.
- 시스템 관리 비용을 낮춘다.
- 성능 최적화에 도움을 준다.
- 소프트웨어 재사용성을 높인다.
- 큰 시스템을 제작하는 난이도를 낮춰준다.

### 접근제한자 (캡슐화를 위한 장치)
- `클래스와 인터페이스의 접근 범위`
    - **package-private**: 같은 패키지 내에서만 사용 가능
    - **public**: 공개 API
- `멤버(필드, 메서드, 중첩 클래스, 중첩 인터페이스)의 접근 범위`
    - **private:** 멤버를 선언한 톱레벨 클래스
    - **package-private**: 멤버가 소속된 패키지 안의 모든 클래스
        - 단, 인터페이스의 멤버는 기본적으로 public이 적용
    - **protected:** 이 멤버를 선언한 클래스와 그 하위 클래스에서도 접근 가능
    - **public:** 접근 제한이 없음(같은 프로젝트 내)

### 주의 사항
- 상위 클래스의 메서드를 재정의할 때는 그 접근 수준을 상위 클래스에서보다 좁게 설정할 수 없다.
    - 컴파일 오류 발생, 리스코프 치환 원칙 위배
- 테스트만을 위해 클래스, 인터페이스, 멤버를 공개 API로 만들어서는 안 된다.
    - private멤버를 package-private까지 풀어주는 것은 허용할 수 있지만 그 이상은 안된다.
- public 클래스의 인스턴스 필드는 되도록 public이 아니어야 한다. (아이템 16)
- 클래스에서 public static final 배열 필드를 두거나 이 필드를 반환하는 접근자 메서드를 제공해서는 안된다.
  ```java
  // 보안 허점이 숨어 있다.
  public static final Thing[] VALUES = { ... };
  ```
  ```java
  // 해결책 1. public 배열을 private으로 만들고 불변 리스트 추가
  private static final Thing[] PRIVATE_VALUES = { ... };
  public static final List<Thing> VALUES = 
      Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));
  ```
  ```java
    // 해결책 2. public 배열을 private으로 만들고 그 복사본을 반환하는 public 메서드 추가(방어적 복사)
    private static final Thing[] PRIVATE_VALUES = { ... };
    public static final Thing[] values() { 
        return PRIVATE_VALUES.clone();
    }
  ```

## 아이템 16. 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라
> public 클래스는 절대 가변 필드를 직접 노출해서는 안 된다.

  ```java
  // 이처럼 퇴보한 클래스는 public이어서는 안 된다!
  class Point {
    public double x;
    public double y;
  }
  ```

  ```java
  // 접근자와 변경자(mutator) 메서드를 활요해 데이터를 캡슐화한다.
  class Point {
    ...
    
    public double getX() { return x; }
    public double getY() { return y; }
        
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
  }
  ```

## 아이템 17. 변경 가능성을 최소화하라
> getter가 있다고 해서 무조건 setter를 만들지는 말자.  
> 클래스는 꼭 필요한 경우가 아니라면 불변이어야 한다.

- 불변 클래스란 그 인스턴스의 내부 값을 수정할 수 없는 클래스

### 자바 플랫폼 라이브러리의 불변 클래스
- String, 기본 타입이 박싱된 클래스들, BigInteger, BigDecimal
- BigInteger, BigDecimal은 재정의할 수 있게 설계되었다. 방어적으로 복사해 사용해야 한다.
```java
  // BigInteger를 방어적으로 복사해서 사용하는 예제
  public static BigInteger safeIntstance(BigInteger val) {
    return val.getClass() == BigInteger.class ?
        val : new BigInteger(val.toByteArray());
  }
```

### 클래스를 불변으로 만들때의 다섯 가지 규칙
- 객체의 상태를 변경하는 메서드(변경자)를 제공하지 않는다.
- 클래스를 확장할 수 없도록 한다.
- 모든 필드를 final로 선언한다.
- 모든 필드를 private으로 선언한다.
- 자신 외에는 내부의 가변 컴포넌트에 접근할 수 없도록 한다.

```java
  // 생성자 대신 정적 팩터리를 사용한 불변 클래스
  public class Complex {
    private final double re;
    private final double im;
    
    private Complex(double re, double im) {
      this.re = re;
      this.im = im;
    }
    
    public static Complex valueOf(double re, double im) {
      return new Complex(re, im);
    }
    ...
  }
  ```

## 아이템 18. 상속보다는 컴포지션을 사용하라
> 상속은 강력하지만 캡슐화를 해친다는 문제가 있다.  
> 상속은 상위 클래스와 하위 클래스가 순수한 is-a 관계일 때만 써야 한다.
> 상속의 취약점을 피하려면 상속 대신 컴포지션과 전달을 사용하자.  
> 특히 래퍼 클래스로 구현할 적당한 인터페이스가 있더만 더욱 그렇다. 래퍼 클래스는 하위 클래스보다 견고하고 강력하다.

## 아이템 19. 상속을 고려해 설계하고 문서화하라. 그러지 않았다면 상속을 금지하라
> 상속용 클래스를 설계할 때 클래스 내부에서 스스로를 어떻게 사용하는지(자기사용 패턴) 모두 문서로 남겨야 한다.  
> 문서화한 것은 그 클래스가 쓰이는 한 반드시 지켜야 한다.  
> 클래스를 확장해야 할 명확한 이유가 떠오르지 않으면 상속을 금지하는 편이 나을 것이다.  
> 상속을금지하려면 클래스를 final로 선언하거나 생성자 모두를 외부에서 접근할 수 없도록 만들면 된다.

## 아이템 20. 추상 클래스보다는 인터페이스를 우선하라
> 일반적으로 다중 구현용 타입으로는 인터페이스가 가장 적합하다.
> 복잡한 인터페이스라면 구현하는 수고를 덜어주는 골격 구현을 함께 제공하는 방법을 꼭 고려해보자.
> 골격 구현은 '가능한 한' 인터페이스의 디폴트 메서드로 제공하여 그 인터페이스를 구현한 모든 곳에서 활용하도록 하는 것이 좋다.
> 인터페이스에 걸려 있는 구현상의 제약 때문에 골격 구현을 추상 클래스로 제공하는 경우가 더 흔하다.

- 추상 클래스가 정의한 타입을 구현하는 클래스는 반드시 추상 클래스의 하위 클래스가 되어야 한다.
- 반면 인터페이스는 기존 클래스에도 손쉽게 새로운 인터페이스를 구현해넣을 수 있다.
- 인터페이스는 `믹스인(mixin)` 정의에 안성맞춤이다.
- 인터페이스로는 계층구조가 없는 타입 프레임워크를 만들 수 있다.
  ```java
  // 가수 인터페이스
  public interface Singer {
    AudioClip sing(Song s);  
  }
  
  // 작곡가 인터페이스
  public interface Songwriter {
    Song compose(int chartPosition);
  }
  ```
  ```java
  // 가수와 작곡가 인터페이스 모두를 확장하고 새로운 메서드까지 추가한 제3의 인터페이스
  public interface SingerSongwriter extends Singer, Songwriter {
    AudioClip strum();
    void actSensitive();  
  }
  ```

## 아이템 21. 인터페이스는 구현하는 쪽을 생각해 설계하라
> 생각할 수 있는 모든 상황에서 불변식을 해치지 않는 디폴트 메서드를 작성하기란 어려운 법이다.
- 디폴트 메서드는 (컴파일에 성공하더라도) 기존 구현체에 런타임 오류를 일으킬 수 있다.
- 인터페이스를 릴리스한 후라도 결함을 수정하는 게 가능한 경우도 있겠지만, 절대 그 가능성에 기대서는 안된다.

```java
  // java 8의 Collection 인터페이스에 추가된 디폴트 메서드
  default boolean removeIf(Predicate<? super E> filter) {
    Objects.requireNonNull(filter);
    boolean result = false;
    for (Iterator<E> it = iterator(); it.hasNext(); ) {
      if (filter.test(it.next())) {
        it.remove();
        result = true;
      }
    }
    return result;
  }
  ```

## 아이템 22. 인터페이스는 타입을 정의하는 용도로만 사용하라
> 상수 인터페이스 안티패턴은 인터페이스를 잘못 사용한 예다.  
> 상수 공개용 수단으로 사용하지 말자.

- 클래스 내부에서 사용하는 상수는 외부 인터페이스가 아니라 내부 구현에 해당한다.
```java
  // 상수 인터페이스 안티패턴 - 사용금지!
  public interface PhysicalConstants {
    // 아보가드로 수 (1/몰)
    static final double AVOGADROS_NUMBER = 6.022_140_857e23;
    
    // 볼츠만 상수 (J/K)
    static final double BOLTZMANN_CONSTANT = 1.380_648_52e-23;
    
    // 전자 질량 (kg)
    static final double ELECTRON_MASS = 9.109_383_56e-31;
  }
  ```
- 특정 클래스나 인터페이스와 강하게 연관된 상수가 아니라면 인스턴스화할 수 없는 유틸리티 클래스에 담아 공개 
```java
  // 상수 유틸리티 클래스
  public class PhysicalConstants {
    private PhysicalConstants() { } // 인스턴스화 방지
    
    // 아보가드로 수 (1/몰)
    public static final double AVOGADROS_NUMBER = 6.022_140_857e23;
    
    // 볼츠만 상수 (J/K)
    public static final double BOLTZMANN_CONSTANT = 1.380_648_52e-23;
    
    // 전자 질량 (kg)
    public static final double ELECTRON_MASS = 9.109_383_56e-31;
  }
  ```

## 아이템 23. 태그 달린 클래스보다는 클래스 계층구조를 활용하라
> 태그 달린 클래스를 써야 하는 상황은 거의 없다.  
> 새로운 클래스를 작성하는 데 태그 필드가 등장한다면 태그를 없애고 계층구조로 대체하는 방법을 생각해보자.  
> 기존 클래스가 태그 필드를 사용하고 있다면 계층구조로 리팩터링하는 걸 고민해보자.
 
```java
// 태그 달린 클래스를 클래스 계층구조로 변환
abstract class Figure {
  abstract double area();
}

// 원 클래스
class Circle extends Figure {
  final double radius;
  
  Circle(double radius) { 
    this.radius = radius; 
  }
  
  @Override 
  double area() {
    return Math.PI * (radius * radius);
  }
}

// 사각형 클래스
class Rectangle extends Figure {
  final double length;
  final double width;
  
  Rectangle(double length, double width) {
    this.length = length;
    this.width = width;
  }
  
  @Override
  double area() {
    return length * width;
  }
}

// 정사각형 클래스
class Square extends Rectangle {
  Square(double side) { // 가로, 세로 길이가 동일
    super(side, side);
  }
}
  ```

## 아이템 24. 멤버 클래스는 되도록 static으로 만들라
> 중첩 클래스(nested class)란 다른 클래스 안에 정의된 클래스를 말한다.
> 중첩 클래스는 자신을 감싼 클래스에서만 쓰여야 하며, 그 외의 쓰임새가 있다면 톱레벨 클래스로 만들어야 한다.
- 중첩 클래스의 종류
  - 정적 멤버 클래스(static)
  - (비정적) 멤버 클래스(inner class)
  - 익명 클래스(inner class)
  - 지역 클래스(inner class)

## 아이템 25. 톱레벨 클래스는 한 파일에 하나만 담으라
> 소스 파일 하나에는 반드시 톱레벨 클래스(인터페이스)를 하나만 담자.
> 이 규칙만 따른다면 컴파일러가 한 클래스에 대한 정의를 여러개 만들어 내는 일은 사라진다.

  ```java
  // 두 클래스가 한 파일에 정의되었다 - 따라 하지 말 것!
  class Utensil {
    static final String NAME = "pan";
  }
  class Dessert {
    static final String NAME = "cake";
  }
  ```
  ```java
  // 톱레벨 클래스들을 정적 멤버 클래스로 바꿔본 모습
  public class Test {
    public static void main(String[] args) {
      System.out.println(Utensil.NAME + Dessert.NAME);
    }
    
    private static class Utensil {
      static final String NAME = "pan";
    }
    private static class Dessert {
      static final String NAME = "cake";
    }
  }
  ```
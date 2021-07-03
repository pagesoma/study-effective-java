# 3장. 모든 객체의 공통 메서드
## 다루는 것
- `Object`에서 final이 아닌 메서드들을 언제 어떻게 `재정의`해야 하는지

## 아이템 목록
10. `equals`는 일반 규약을 지켜 재정의하라
11. equals를 재정의하려거든 `hashCode`도 재정의하라
12. `toString`을 항상 재정의하라
13. `clone` 재정의는 주의해서 진행하라
14. `Comparable`을 구현할지 고려하라

## 아이템 10. equals는 일반 규약을 지켜 재정의하라
- 재정의하지 않으면 클래스의 인스턴스는 오직 `자기 자신`과만 같게 된다.

### equals를 재정의하지 않는 것이 최선인 상황
- 각 인스턴스가 본질적으로 고유하다. (동작하는 개체를 표현하는 클래스, 예: Thread)
- 인스턴스의 `논리적 동치성(logical equality)`을 검사할 일이 없다.
- 상위 클래스에서 재정의한 equals가 하위 클래스에도 딱 들어맞는다. (Set, List, Map)
- 클래스가 private이거나 package-private이고 equals 메서드를 호출할 일이 없다.
  ```java
   // equals가 실수로라도 호출되는 걸 막고 싶다면 다음처럼 구현
   @Override public boolean equals(Object o) {
     throw new AssertionError(); // 호출 금지!
   }
   ```

### equals를 재정의해야 하는 상황
- `객체 식별성`(두 객체가 물리적으로 같은가)이 아니라 `논리적 동치성`을 확인해야 하는데, **상위 클래스의 equals가 논리적 동치성을 비교하도록 재정의되지 않았을 때**
- `Enum`이나 `인스턴스 통제 클래스`(아이템1)
   - 논리적으로 같은 인스턴스가 2개 이상 만들어지지 않으니 논리적 동치성과 객체 식별성이 사실상 똑같은 의미가 되므로 재정의하지 않아도 된다.
   - 따라서, Object의 equals가 논리적 동치성까지 확인해준다고 볼 수 있다.
   
### equals를 재정의할 때 따라야할 일반 규약
   > equals 메서드는 `동치관계`를 구현하며, 다음을 만족한다.
   > - `반사성 (reflexivity)`: null이 아닌 모든 참조 값 x에 대해, x.equals(x)는 true다.
   > - `대칭성 (symmetry)`: null이 아닌 모든 참조 값 x, y에 대해, x.equals(y)가 true면 y.equals(x)도 true다. 
   > - `추이성 (transitivity)`: null이 아닌 모든 참조 값 x,y,z에 대해, x.equals(y)가 true이고 y.equals(z)도 true면 x.equals(z)도 true다.
   > - `일관성 (consistency)`: null이 아닌 모든 참조 값 x,y에 대해 x.equals(y)를 반복해서 호출하면 항상 true를 반환하거나 항상 false를 반환한다.
   > - `null-아님`: null이 아닌 모든 참조 값 x에 대해, x.equals(null)은 false다.
   ```java
   // 명시적 null 검사 - 필요 없다!
   @Override public boolean equals(Object o) {
      if (o == null) return false;
   }
  // 묵시적 null 검사 - 이쪽이 낫다.
  @Override public boolean equals(Object o) {
     if (!(o instanceof MyType)) return false;  // instanceof는 o가 null이면 false를 반환한다.
     MyType mt = (MyType) o;
  }
   ```

### equals 메서드 단계별 구현 방법
1. == 연산자를 사용해 입력이 자기 자신의 참조라면 `true` 반환(단순한 성능 최적화용)
2. instanceof 연산자로 입력이 올바른 타입인지 확인한다. 아니라면 `false` 반환
3. 입력을 올바른 타입으로 형변환한다.
4. 입력 객체와 자기 자신의 대응되는 `핵심` 필드들이 모두 일치하면 `true`, 하나라도 다르면 `false`반환
   - float, double을 제외한 `기본 타입 필드`: == 연산자로 비교 
   - `참조 타입 필드`: equals 메서드로 비교
   - `float`: Float.compare(float, float)로 비교 (Float.NaN, -0.0f, 특수한 부동소수 값 등을 다뤄야하기 때문)
   - `double`: Double.compare(double, double)로 비교
```java
  // 전형적인 equals 메서드
  @Override public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof PhoneNumber)) return false;
    PhoneNumber pn = (PhoneNumber) o;
    return pn.lineNum == lineNum && pn.prefix == prefix 
        && pn.areaCode == areaCode; 
  }
   ```

### 주의사항
- equals를 재정의할 땐 hashCode도 반드시 재정의하자(아이템 11)
- 너무 복잡하게 해결하려 들지 말자. (필드의 동치성만 검사)
- Object 외의 타입을 매개변수로 받는 equals 메서드는 선언하지 말자.

## 아이템 11. equals를 재정의하려거든 hashCode도 재정의하라
- 그렇지 않으면 hashCode 일반 규약을 어기게 되어 해당 클래스의 인스턴스를 `HashMap`이나 `HashSet` 같은 컬렉션의 원소로 사용할 때 문제를 일으킨다.
- `HashMap`은 해시코드가 다른 엔트리끼리는 동치성 비교를 시도조차 하지 않도록 최적화되어 있다.
> Object 명세에서 발췌한 규약
> - equals 비교에 사용되는 정보가 변경되지 않았다면, 해플리케이션이 실행되는 동안 그 객체의 hashCode 메서드는 몇 번을 호출해도 일관되게 항상 같은 값을 반환해야한다. 단, 애플리케이션을 다시 실행한다면 이 값이 달라져도 상관없다.
> - equals(Object)가 두 객체를 같다고 판단했다면, 두 객체의 hashCode는 똑같은 값을 반환해야 한다.
> - equals(Object)가 두 객체를 다르다고 판단했다더라도, 두 객체의 hashCode가 서로 다른 값을 반환할 필요는 없다.
```java
  // 전형적인 hashCode 메서드
  @Override public int hashCode() {
    int result - Short.hashCode(aresCode);
    result = 31 * result + Short.hashCode(prefix);  // 31은 홀수이면서 소수(prime)이다.
    result = 31 * result + Short.hashCode(lineNum); // 31 * i는 (i << 5) -i와 같고 요즘 VM들은 이런 최적화를 자동으로 해준다.
    return result;
  }
```
- `Objects` 클래스는 해시코드를 계산해주는 정적 메서드인 `hash`를 제공한다.
   - 하지만 입력 중 기본 타입이 있다면 `오토박싱`과 `언박싱`을 거치기 때문에 전형적인 구현보다 `성능에 불리`하다.

## 아이템 12. toString을 항상 재정의하라
- toString을 잘 구현한 클래스는 사용하기에 훨씬 즐겁고, 그 클래스를 사용한 시스템은 `디버깅하기 쉽다.`
- 실전에서 toString은 그 객체가 가진 `주요 정보` 모두를 반환하는 게 좋다.
- `정적 유틸리티 클래스`(아이템 4)는 toString을 `제공할 이유가 없다.`
- 대부분의 `열거 타입`(아이템 34)도 자바가 이미 완벽한 toString을 제공하니 따로 `재정의하지 않아도 된다.`
- 하위 클래스들이 공유해야 할 문자열 표현이 있는 `추상 클래스`라면 toString을 `재정의해줘야 한다.`



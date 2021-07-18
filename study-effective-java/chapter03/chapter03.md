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
> 꼭 필요한 경우가 아니면 equals를 재정의하지 말자.  
> 재정의하지 않으면 클래스의 인스턴스는 오직 `자기 자신`과만 같게 된다.
> 많은 경우에 Object의 equals가 비교를 정확히 수행해준다.  
> 재정의해야 할 때는 그 클래스의 핵심 필드 모두를 빠짐없이, 다섯 가지 규약을 확실히 지켜가며 비교해야 한다.

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
> 그렇지 않으면 hashCode 일반 규약을 어기게 되어 해당 클래스의 인스턴스를 `HashMap`이나 `HashSet` 같은 컬렉션의 원소로 사용할 때 문제를 일으킨다.
> `HashMap`은 해시코드가 다른 엔트리끼리는 동치성 비교를 시도조차 하지 않도록 최적화되어 있다.

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
> 모든 구체 클래스에서 Object의 toString을 재정의하자.
> 상위 클래스에서 이미 알맞게 재정의한 경우는 예외

- toString을 잘 구현한 클래스는 사용하기에 훨씬 즐겁고, 그 클래스를 사용한 시스템은 `디버깅하기 쉽다.`
- 실전에서 toString은 그 객체가 가진 `주요 정보` 모두를 반환하는 게 좋다.
- `정적 유틸리티 클래스`(아이템 4)는 toString을 `제공할 이유가 없다.`
- 대부분의 `열거 타입`(아이템 34)도 자바가 이미 완벽한 toString을 제공하니 따로 `재정의하지 않아도 된다.`
- 하위 클래스들이 공유해야 할 문자열 표현이 있는 `추상 클래스`라면 toString을 `재정의해줘야 한다.`

## 아이템 13. clone 재정의는 주의해서 진행하라
> 새로운 인터페이스를 만들 때는 절대 Cloneable확장해서는 안된다.  
> 새로운 클래스도 이를 구현해서는 안된다. (final 클래스라면 위험이 크지 않지만 성능 최적화 관점에서 검토한 후 문제없을 때만 드물게 허용)  
> 기본 원칙은 `복제 기능은 생성자와 팩터리를 이용하는 게 최고`라는 것  
> 단, `배열`만은 clone 메서드 방식이 `가장 깔끔한`, 이 규칙의 합당한 예외라 할 수 있다.

- Cloneable 인터페이스는 Object의 protected 메서드인 clone의 동작 방식을 결정한다.
   - `Cloneable`을 구현한 클래스의 인스턴스에서 `clone`을 호출하면 `그 객체의 필드들을 하나하나 복사한 객체를 반환`한다.
   - 그렇지 않은 클래스의 인스턴스에서 호출하면 `CloneNotSupportedException`을 던진다.
   ```java
     // 가변 상태를 참조하는 클래스용 clone 메서드
     @Override public Stack clone() {
       try {
         Stack result = (Stack) super.clone();
         result.elements = elements.clone(); // 재귀적 호출, 배열의 clone은 런타임 타입과 컴파일타임 타입 모두가 원본 배열과 똑같은 배열을 반환한다.
         return result;
       } catch (CloneNotSupportedException e) {
         throw new AssertionError();
       }
     }
   ```
- `HashTable` 내부는 `버킷들의 배열`이고, 각 버킷은 `key-value` 쌍을 담는 `linked list`의 첫 번째 `entry`를 참조한다.
   - HashTable의 clone의 경우 HashTable.Entry를 재귀적 호출로 clone한다면 복제본은 원본과 같은 연결 리스트를 참조하여 예기치 않게 동작할 수 있다. 
   - `HashTable.Entry`는 `깊은복사(deep copy)`를 지원하도록 보강되었고 deepCopy를 재귀 호출 대신 반복자를 써서 순회하는 방향으로 구현해야 한다.
- Cloneable을 구현한 Thread-Safe 클래스를 작성할 때는 clone 메서드 역시 적절히 동기화해줘야 한다(아이템 78)
- 복사 생성자와 복사 팩터리라는 더 나은 객체 복사 방식을 제공할 수 있다.
   - `복사 생성자` - 단순히 자신과 같은 클래스의 인스턴스를 인수로 받는 생성자
     ```java
     public Yum(Yum yum) { ... };
     ```
   - `복사 팩터리` - 복사 생성자를 모방한 정적 팩터리(아이템 1)
     ```java
     public static Yum newInstance(Yum yum) { ... };
     ```
   - 인터페이스 타입의 인스턴스를 인수로 받는 복사 생성자와 복사 생성자의 명칭은 `변환 생성자`와 `변환 팩터리`이다.

## 아이템 14. Comparable을 구현할지 고려하라
> `순서`를 고려해야 하는 값 클래스를 작성한다면 꼭 Comparable 인터페이스를 구현하여, 그 인스턴스들을 쉽게 정렬하고, 검색하고, `비교 기능`을 제공하는 컬렉션과 어우러지도록 해야 한다.

- `Comparable` 인터페이스에는 `compareTo`라는 유일무이한 메서드가 있다.
- compareTo 메서드로 수행하는 `동치성 검사`도 equals 규약과 똑같이 `반사성`, `대칭성`, `추이성`을 충족해야 한다.
   - 비교를 활용하는 클래스의 예 - TreeSet, TreeMap
   - 검색과 정렬 알고리즘을 활용하는 유틸리티 클래스 - Collections, Arrays
      ```java
      // Comparable을 구현한 객체들의 배열은 아래와 같이 정렬 
      Arrays.sort(a);
      ```
- `compareTo` 메서드에서 필드의 값을 비교할 때 <와 > 연산자는 쓰지 말아야 한다.
   - 그 대신 박싱된 기본 타입 클래스가 제공하는 `정적 compare 메서드`나 Comparator 인터페이스가 제공하는 `비교자 생성 메서드`를 사용하자.
   ```java
    // 기본 타입 필드가 여럿일 때의 비교자
    public int compareTo(PhoneNumber pn) {
      int result = Short.compare(areaCode, pn.areaCode);  // 가장 중요한 필드
      if (result == 0) {
        result = Short.compare(prefix, pn.prefix);        // 두 번째로 중요한 필드
        if (result == 0) {
          result = Short.compare(lineNum, pn.lineNum);    // 세 번째로 중요한 필드
        }
      }
      return result;
    }
   
    // Java 8 - 비교자 생성 메서드를 활용한 비교자 (위 방식보다 성능이 떨어짐)
    private static final Comparator<PhoneNumber> COMPARATOR =
            comparingInt((PhoneNumber pn) -> pn.areaCode)
              .thenComparingInt(pn -> pn.prefix)
              .thenComparingInt(pn -> pn.lineNum);
  
    public int compareTo(PhoneNumber pn) {
      return COMPARATOR.compare(this, pn);
    }  
   ```
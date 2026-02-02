# HMDamageLib (Paper 1.20.1)

각종 플러그인에서 공통으로 사용할 스탯/데미지 코어 라이브러리입니다.

## 빌드

```bash
mvn -q clean package
```

생성 파일: `target/HMDamageLib-1.0.0.jar`

## 제공 기능

- `StatService`: Provider 등록/해제, 스탯 스냅샷 캐싱, 값 조회
- `DamageService`: 공격자/피격자 스탯 기반 데미지 계산
- 커스텀 이벤트
  - `StatRecalculateEvent`
  - `CritRollEvent`
  - `DamageComputeEvent`

## 다른 플러그인에서 사용

의존성은 `compileOnly`로 추가하고, 런타임에는 서버에 HMDamageLib가 같이 있어야 합니다.

### Maven (다른 플러그인)

```xml
<dependency>
  <groupId>org.haemin</groupId>
  <artifactId>HMDamageLib</artifactId>
  <version>1.0.0</version>
  <scope>provided</scope>
</dependency>
```

`plugin.yml`에 추가:

```yml
depend: [HMDamageLib]
```

### StatProvider 등록 예시

```java
public final class MyRuneProvider implements StatProvider {
    @Override
    public String id() {
        return "runes";
    }

    @Override
    public Collection<StatModifier> provide(Player player) {
        return List.of(
                new SimpleStatModifier("rune:atk", StatType.ATTACK_DAMAGE, Operation.ADD, 10.0, 10),
                new SimpleStatModifier("rune:crit", StatType.CRIT_CHANCE, Operation.ADD, 0.05, 10)
        );
    }
}
```

```java
StatService stat = Bukkit.getServicesManager().load(StatService.class);
stat.registerProvider(new MyRuneProvider());
stat.markDirty(player);
```

### 데미지 계산 예시(스킬 등)

```java
DamageService dmg = Bukkit.getServicesManager().load(DamageService.class);
DamageResult result = dmg.compute(attacker, victim, 12.0, EntityDamageEvent.DamageCause.CUSTOM);
```

## 디버그 명령어

- `/hmdamagelib stats [player]`


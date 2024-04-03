# 🔗 concurrent-unique API

This project is a **small** Java API offering a flexible and extensible solution for generating unique identifiers of
various types. Designed primarily as an interface framework, it can be integrated into projects requiring the generation
of identifiers, whatever their type or security and performance requirements.

**Summary**:

- [⚙️ How do you use it](#%EF%B8%8F-how-do-you-use-it)
    - [🏭 RandomGeneratorSupplier](#-randomgeneratorsupplier)
    - [🗂️ Generator Interfaces](#%EF%B8%8F-generator-interfaces)
        - [🆔 UUIDGenerator](#-uuidgenerator)
        - [🔤 AlphanumericGenerator](#-alphanumericgenerator)
        - [🔢 IntegerIncrementerGenerator & LongIncrementerGenerator](#-integerincrementergenerator--longincrementergenerator)
        - [🗑️ EmptyGenerator](#%EF%B8%8F-emptygenerator)
    - [🔒 Make Verified Generators](#-make-verified-generators)
    - [🛠️ Make Custom Generators](#%EF%B8%8F-make-custom-generators)
        - [🔓 Make Custom Verified Generator](#-make-custom-verified-generator)
- [📄 License](#-license)

*Note: This project is not particularly designed to be used by anyone but I have made it publicly available anyway.
As the project is not intended to be maintained, the README may not be up-to-date.*

# ⚙️ How do you use it?

To use the project as a dependency, you can add it using [Maven](https://maven.apache.org/)
or [Gradle](https://gradle.org/).
<br>**Last version**: [![Release](https://jitpack.io/v/Darkkraft/concurrent-unique.svg)](https://jitpack.io/#Darkkraft/concurrent-unique)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.Darkkraft:concurrent-unique:VERSION'
}
```

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.Darkkraft</groupId>
        <artifactId>concurrent-unique</artifactId>
        <version>VERSION</version>
    </dependency>
</dependencies>
```

## 🏭 RandomGeneratorSupplier

This is an interface for accessing instances of `java.util.random.RandomGenerator`. You can implement it yourself to
provide a custom RandomGenerator or use the default implementations. Here's how to build a default implementation:

```java
// Using ThreadLocalRandom
RandomGeneratorSupplier.build(RandomGeneratorSupplier.Type.THREAD_LOCAL);
// Using custom sequential random (not thread-safe)
RandomGeneratorSupplier.build(RandomGeneratorSupplier.Type.FAST_SEQUENTIAL);
// Using SecureRandom
RandomGeneratorSupplier.build(RandomGeneratorSupplier.Type.SECURE);
// Using custom RandomGenerator
RandomGeneratorSupplier.build(RandomGenerator.of("L32X64MixRandom"));
```

## 🗂️ Generator interfaces

The most important interface is [UniqueGenerator](/src/main/java/be/darkkraft/concurrentunique/UniqueGenerator.java),
which contains just one method to implement: `UniqueGenerator<T>#generate`.
<br>Then there's
the [VerifiedUniqueGenerator](/src/main/java/be/darkkraft/concurrentunique/verified/VerifiedUniqueGenerator.java)
interface, which is a child class of `UniqueGenerator`. Its purpose is to generate an identifier until it is one that
has never been generated before.

Although this project essentially contains interfaces, there are default implementations.

### 🆔 UUIDGenerator

This is an interface for building `java.util.UUID` generators. Here are a few examples:

```java
final UUIDGenerator generator = UUIDGenerator.build(RandomGeneratorSupplier.Type.SECURE);
final UUIDGenerator generator = UUIDGenerator.build(RandomGenerator.of("L32X64MixRandom"));
```

### 🔤 AlphanumericGenerator

This is an interface for building alphanumeric `java.lang.String` generators. Here are a few examples:

```java
final RandomGeneratorSupplier.Type type = RandomGeneratorSupplier.Type.SECURE;
// Builds a generator to generate 10-character alphanumeric strings
final AlphanumericGenerator generator = AlphanumericGenerator.build(10, type);
// It is possible to use the parent interface
final UniqueGenerator<String> generator = AlphanumericGenerator.build(5, type);
```

### 🔢 IntegerIncrementerGenerator & LongIncrementerGenerator

This is an interface for building atomic `int` and `long` generators. Here are a few examples:

```java
final IntegerIncrementerGenerator generator = new IntegerIncrementerGenerator();
final int value = generator.generateInt();
final LongIncrementerGenerator generator = new LongIncrementerGenerator();
final long value = generator.generateLong();
```

### 🗑️ EmptyGenerator

There is an empty generator implementation, mainly for testing purposes.

```java
final UniqueGenerator<String> generator = UniqueGenerator.empty();
```

## 🔒 Make verified generators

For a small project or for testing purposes, you can use default implementations that work with cache. Simply by keeping
in memory entries that have already been generated, whether concurrently or not.

```java
// Build a simple generator (not thread-safe)
final UUIDGenerator generator = UUIDGenerator.build(RandomGeneratorSupplier.Type.FAST_SEQUENTIAL);
// Build a concurrent verified generator with full cache
// The parameter "5" indicates the maximum number of retries after a failure.
final UniqueGenerator<UUID> verifiedGenerator = new ConcurrentFullCacheGenerator<>(generator, 5);
final UUID verifiedUUID = verifiedGenerator.generate();

// Not thread-safe verified generator
final UniqueGenerator<UUID> verifiedGenerator = new SequentialFullCacheGenerator<>(generator, 5);
```

## 🛠️ Make custom generators

You can use the built-in interfaces to create your own generators. As the code is fairly simple, I recommend that you
take a look at the default implementations to see how they work.

Here's a simple example for generating alphabetic `java.lang.String`:

```java
public record MyGenerator(RandomGeneratorSupplier randomGeneratorSupplier) implements UniqueGenerator<String> {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int GENERATED_LENGTH = 10;

    @Override
    public String generate() {
        final StringBuilder builder = new StringBuilder(GENERATED_LENGTH);
        for (int i = 0; i < GENERATED_LENGTH; i++) {
            final int index = this.randomGeneratorSupplier.getRandomGenerator().nextInt(CHARS.length());
            builder.append(CHARS.charAt(index));
        }
        return builder.toString();
    }

}
```

```java
final MyGenerator generator = new MyGenerator(RandomGeneratorSupplier.build(RandomGeneratorSupplier.Type.SECURE));
final String generated = generator.generate();
```

### 🔓 Make custom verified generator

You can use the interface directly to create your own VerifiedUniqueGenerator, or use the default abstract class:

```java
public final class MyVerifiedGenerator extends AbstractVerifiedUniqueGenerator<String> {

    public MyVerifiedGenerator(final @NotNull UniqueGenerator<String> generator, final int maxRetry) {
        super(generator, maxRetry);
    }

    @Override
    public boolean isAlreadyExists(final @NotNull String generated) {
        // Checks that the identifier is unique, for example using a database query.
        return true;
    }

}
```

Or simply wrapping a predicate:

```java
final UUIDGenerator generator = UUIDGenerator.build(RandomGeneratorSupplier.Type.SECURE);
final int maxRetry = 5;
final UniqueGenerator<UUID> verifiedGenerator = generator.toVerified(maxRetry, uuid -> this.mySet.contains(uuid));
```

# 📄 License

This project is made available under the MIT License. Feel free to use, modify, and distribute the code as you see fit,
keeping in mind the terms and conditions of the license.
# 🔗 concurrent-unique API

This project is a **small** Java API offering a flexible and extensible solution for generating unique identifiers of
various types. Designed primarily as an interface framework, it can be integrated into projects requiring the generation
of identifiers, whatever their type or security and performance requirements.

**Summary**:

- [⚙️ How it works](#-how-does-it-work)
    - [🏭 RandomGeneratorSupplier](#-randomgeneratorsupplier)
    - [🗂️ Generator Interfaces](#-generator-interfaces)
        - [🆔 UUIDGenerator](#-uuidgenerator)
        - [🔤 AlphanumericGenerator](#-alphanumericgenerator)
        - [🔢 IntegerIncrementerGenerator & LongIncrementerGenerator](#-integerincrementergenerator--longincrementergenerator)
        - [🗑️ EmptyGenerator](#-emptygenerator)
    - [🔒 Make Verified Generators](#-make-verified-generators)
    - [🛠️ Make Custom Generators](#-make-custom-generators)
        - [🔓 Make Custom Verified Generator](#-make-custom-verified-generator)
- [📄 License](#-license)

*Note: This project is not particularly designed to be used by anyone but I have made it publicly available anyway.*
# ⚙️ How does it work?

Here are a few explanations of how the project works

## 🏭 RandomGeneratorSupplier

This is an interface for accessing instances of `java.util.random.RandomGenerator`. You can implement it yourself to
provide a custom RandomGenerator or use the default implementations. Here's how to build a default implementation:

```java
// Using ThreadLocalRandom
final RandomGeneratorSupplier supplier = RandomGeneratorSupplier.build(RandomGeneratorSupplier.Type.THREAD_LOCAL);
// Using custom sequential random (not thread-safe)
final RandomGeneratorSupplier supplier = RandomGeneratorSupplier.build(RandomGeneratorSupplier.Type.FAST_SEQUENTIAL);
// Using SecureRandom
final RandomGeneratorSupplier supplier = RandomGeneratorSupplier.build(RandomGeneratorSupplier.Type.SECURE);
// Using custom RandomGenerator
final RandomGeneratorSupplier supplier = RandomGeneratorSupplier.build(RandomGenerator.of("L32X64MixRandom"));
```

## 🗂️ Generator interfaces

The most important interface is [UniqueGenerator](/src/main/java/be/darkkraft/concurrentunique/UniqueGenerator.java),
which contains just one method: `UniqueGenerator<T>#generate`.
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
// Builds a generator to generate 10-character alphanumeric strings
final AlphanumericGenerator generator = AlphanumericGenerator.build(10, RandomGeneratorSupplier.Type.SECURE);
// It is possible to use the parent interface
final UniqueGenerator<String> generator = AlphanumericGenerator.build(5, RandomGeneratorSupplier.Type.FAST_SEQUENTIAL);
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
final UniqueGenerator<String> generator = EmptyGenerator.get();
```

## 🔒 Make verified generators

For a small project or for testing purposes, you can use default implementations that work with cache. Simply by keeping
in memory entries that have already been generated, whether concurrently or not.

```java
// Build a simple generator (not thread-safe)
final UUIDGenerator generator = UUIDGenerator.build(RandomGeneratorSupplier.Type.FAST_SEQUENTIAL);
// Build a concurrent verified generator with full cache
// The parameter "5" indicates the maximum number of retries after a failure.
final VerifiedUniqueGenerator<UUID> verifiedGenerator = new ConcurrentFullCacheGenerator<>(generator, 5);
final UUID verifiedUUID = verifiedGenerator.generate();

// Not thread-safe verified generator
final VerifiedUniqueGenerator<UUID> verifiedGenerator = new SequentialFullCacheGenerator<>(generator, 5);
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
            builder.append(CHARS.charAt(this.randomGeneratorSupplier.getRandomGenerator().nextInt(CHARS.length())));
        }
        return builder.toString();
    }

}
```

```java
final UniqueGenerator<String> generator =
        new MyGenerator(RandomGeneratorSupplier.build(RandomGeneratorSupplier.Type.SECURE));
final String generated = generator.generate();
```

### 🔓 Make custom verified generator

You can use the interface directly to create your own VerifiedUniqueGenerator, or use the default abstract class :

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

# 📄 License

This project is made available under the MIT License. Feel free to use, modify, and distribute the code as you see fit,
keeping in mind the terms and conditions of the license.
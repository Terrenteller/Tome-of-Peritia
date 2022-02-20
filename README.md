# What is Tome of Peritia?

Tome of Peritia allows experience to be stored on a special book like the item of the same name from Blood Magic: Alchemical Wizardry.

## How do I use Tome of Peritia?

Server:
- Drop the JAR in the `plugins` directory and restart

Client:
- Rename an enchanted book with Mending to "Tome of Peritia"
    - Other enchantments or level of Mending does not matter
- Sneak + \[RMB\] to store experience by level
    - Stores all experience when in the off-hand
- \[RMB\] to withdraw experience by level
    - Withdraws all experience when in the off-hand

## How do I build Tome of Peritia?

Refer to Fabric documentation instead to prepare a development environment for use with an IDE.

**Arch Linux**

```
# pacman -Sy jdk-openjdk
$ export JAVA_HOME=/usr/lib/jvm/java-17-openjdk/
$ ./gradlew build
```

## Legal stuff

Tome of Peritia is licenced under [LGPL 3.0](LICENCE.md) unless where otherwise stated.

NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG.

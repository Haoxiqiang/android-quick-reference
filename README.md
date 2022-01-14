# Android Open Source Reference Plugin

The library inspired by [AndroidSourceViewer](https://github.com/pengwei1024/AndroidSourceViewer)

**It's built with the Gradle and rewritten by kotlin, that's why it's a new repo but not pr.**

**In the rewriting, I have added some features too.**

<!-- Plugin description -->

* You can view Android Open Source in the ide And link the native method to native code.
* Support Quick Search by google/bing/github/stackoverflow
* Quick Open Android CodeSearch/Dev Reference by the Browser.

I found this is a terrible pattern to find the file path from the psi framework after the first commit. I will do more
works to solve the psi problem. Who with relevant practical experience can help me.
<!-- Plugin description end -->

## Install

[![JetBrains Plugins](https://img.shields.io/jetbrains/plugin/v/18369-quick-reference.svg)](https://plugins.jetbrains.com/plugin/18369-quick-reference)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/18369-quick-reference.svg)](https://plugins.jetbrains.com/plugin/18369-quick-reference)
[![Downloads last month](http://phpstorm.espend.de/badge/18369/last-month)](https://plugins.jetbrains.com/plugin/18369-quick-reference)

[Get Plugin From Market](https://plugins.jetbrains.com/plugin/18369-quick-reference/versions/stable/153417)

## Feature

* View native method by click c++ image:

![aosp-native](./screenshots/native-linker.gif)

* You can compare diff version source.

![plugin-diff.png](./screenshots/plugin-diff.png)

* Download android-AOSP-source from the remote.
    * [github.com/aosp-mirror](https://github.com/aosp-mirror/platform_frameworks_base)
    * [sourcegraph.com](https://sourcegraph.com/)
    * [androidxref.com](http://androidxref.com/)

* Quick Android Reference
    * [developer.android.google](https://developer.android.com/reference)

* View Android Source Online
    * [Android Code Search](https://cs.android.com/)

* Quick Search by the mouse right click.
    * google.com
    * bing.com
    * github.com
    * stackoverflow

  ![plugin-menu1](./screenshots/plugin-menu2.png)
  ![plugin-version](./screenshots/plugin-version.png)

## TODO

More works.

- [ ] androidx support(before next weekend).
- [ ] auto jump to the source line.
- [ ] linker more action in the aosp source.
- [x] add native jni mapping db.
- [ ] add java class/method mapping db.
- [ ] support custom quick search menu.
- [ ] android reference support two hosts:`developer.android.com`/`developer.android.google.cn`
- [ ] diff android version. many files location is changed.

## Native Mapping

* [RepoTools](./src/test/java/com/quickref/plugin/git/RepoTools.kt) use for checkout the diff commit ref's native files.
* [NativeDBGenerator](./src/test/java/com/quickref/plugin/git/NativeDBGenerator.kt) native db sql

## License

The project is licensed under the [Apache License 2.0](./LICENSE.txt).

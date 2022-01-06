# Android Reference Intellij Plugin

This library based on [AndroidSourceViewer](https://github.com/pengwei1024/AndroidSourceViewer)

**It's built with the Gradle and rewritten by kotlin, that's why it's a new repo but not pr.**

**In the rewriting, I have added some features too.**

I found the project has a bad pattern to find the file path from psi framework after the first commit.
I will do more works to solve the psi problem.
the one with relevant practical experience can help me.

## Feature

* Quick Search by the mouse right click.
    * google.com
    * bing.com
    * github.com
    * stackoverflow

* Download android-AOSP-source from the remote.
    * [github.com/aosp-mirror](https://github.com/aosp-mirror/platform_frameworks_base)
    * [sourcegraph.com](https://sourcegraph.com/)
    * [androidxref.com](http://androidxref.com/)

* Quick Android Reference
    * [developer.android.google](https://developer.android.com/reference)

* View Android Source Online
    * [Android Code Search](https://cs.android.com/)

## TODO

More works.

- [ ] androidx support(before next weekend).
- [ ] auto jump to the source line.
- [ ] linker more action in the aosp source.
- [ ] add native jni mapping db.
- [ ] add java class/method mapping db.
- [ ] support custom quick search menu.
- [ ] android reference support two hosts:`developer.android.com`/`developer.android.google.cn`

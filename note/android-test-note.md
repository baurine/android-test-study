# Android Test Note

## References

1. [Android Testing Codelab](https://codelabs.developers.google.com/codelabs/android-testing/index.html)
1. [在 Android Studio 中进行单元测试和UI测试](http://www.jianshu.com/p/03118c11c199)，[Origin](https://io2015codelabs.appspot.com/codelabs/android-studio-testing)

## Note 1

Note for [Android Testing Codelab](https://codelabs.developers.google.com/codelabs/android-testing/index.html)

这个例子中的示例工程是个代码组织极好的典范，值得深入研究。实际从这个 Codelab 关于测试的内容收获不多，比如 mockito 居然都没怎么说明怎么使用，反而是加深了对 MVP，Repository 模式，以及 Contract 类的理解。

传统的 MVC 是不方便用来测试的，如果要方便测试，比较适合的模式就是 MVP。

代码结构：

![](./art/android-test-1.png)

1. Repository 广义上属于 MVP 中的 M - Model，它向 Presenter 屏蔽了底层操作 (获取，存储) data (即狭义的 model) 的细节，Repository 定义了操作 data 的接口，然后由不同的具体的 Repository 实现类来实现这些方法。

1. 在 Repository 中调用网络 API 获取数据，或者从数据库读取数据。Presenter 只与 Repository 交互，Repository 相当于 Model 层的对外接口。

1. Contract 类只是简单地把 IView 和 IPresenter 的定义包装在一起，实际上并不是必需的，但它使代码更简洁，避免了多定义一个文件，也减少了命名的烦恼。

1. 在 UI 占主导的客户端 MVP 中，一般来说，V 是入口，因此 V 是主导，因此，一般先有 V，然后由 V 来生成 P，在此例中，甚至由 V 来生成具体的 Repository，并注入到 P 中。而对于服务端 (服务端的 MVC 更像是 MVP) 来说，一般是 P 占主导，先有 P，再由 P 来生成 V。

1. 在 MVP 中，V 和 P 相互引用，直接调用对方的方法，而 P 和 M (在此例中，M 指 Repository)，P 持有 M 的引用，P 直接调用 M 的方法，但 M 直接在方法中，通过参数中的回调接口通知 P，因此，它并不需要持有 P 的引用。比如此例中 NotesRepository 的定义：

       public interface NotesRepository {
           interface LoadNotesCallback {
               void onNotesLoaded(List<Note> notes);
           }

           interface GetNoteCallback {
               void onNoteLoaded(Note note);
           }

           void getNotes(@NonNull LoadNotesCallback callback);

           void getNote(@NonNull String noteId, @NonNull GetNoteCallback callback);

           void saveNote(@NonNull Note note);

           void refreshData();
       }

1. 这个工程按功能分包，而不是按代码类别分包。

1. `Injection.provideNotesRepository()`，注入的手动实现

1. 使用 Falvor，在不同的环境下 (mock 和 prod)，通过 `Injection.provideNotesRepository()` 得到不同的 Repository 实现类。

## Note 2

Note for [在 Android Studio 中进行单元测试和UI测试](http://www.jianshu.com/p/03118c11c199)，[Origin](https://io2015codelabs.appspot.com/codelabs/android-studio-testing)

照着例子练习了一下，虽然是个极简单的例子，也遇到不少坑，最大的坑是，例子中测试在输入框中输入 "Peter"，注意首字母是大写，然而在我的手机上跑测试时，无法输入大写字母 (我也不知道为什么)，导致测试失败，而报的错误信息也很误导人，试了很久才发现是这个问题，把 "Peter" 改成 "peter" 后再能成功。

    Caused by: junit.framework.AssertionFailedError: 'with text: is "Hello, Peter!"' doesn't match the selected view.
    Expected: with text: is "Hello, Peter!"
    Got: "AppCompatTextView{id=2131427422, res-name=textView, visibility=VISIBLE, width=278, height=76, has-focus=false, has-focusable=false, has-window-focus=true, is-clickable=false, is-enabled=true, is-focused=false, is-focusable=false, is-layout-requested=false, is-selected=false, root-is-layout-requested=false, has-input-connection=false, x=64.0, y=64.0, text=Hello, pete!, input-type=0, ime-target=false, has-links=false}"


通过这个例子，对 android 下的测试有了一个比较大概的理解。

首先，代码测试 (与黑盒白盒的手动测试区分) 一般分两种：

- 单元测试
- 功能测试，或者也叫集成测试

单元测试是只对一个单独的方法进行测试，这是我们测试的重点，应该占所有测试的 70% 以上。而功能测试，是对一个功能进行完整的地测试，其中包含了涉及此功能的多个方法的连续调用。

功能测试是单元测试的超集，所用的技术和框架没有太大区别。

对于单元测试和功能测试内部，又细分两种：

- 非 UI 方法的测试
  - 在 JVM 上跑，不需要模拟器或设备
  - 用到技术或框架：JUnit4，Mockito
  - 在 `test` 目录下
  - 命令行命令：`./gradlew test`
- UI 方法的测试
  - 需要模拟器或设备
  - 用到技术或框架：Espresso
  - 在 `androidTest` 目录下
  - 命令行命令：`./gradlew cAT` (cAT - connect Android Test)

对于非 UI 方法的测试，我们用 JUnit4 和 Mockito 就够了，Mockito 用来 mock 对象，比如在 MVP 中，我们对 P 进行单元测试时，需要用 Mockito 来 mock V 和 M。

测试的目标方法分两种

- 有返回值的方法：直接用 JUnit 的 `assertEquals` 系列方法判断返回值是否与预期相符
- 没有返回值的方法：使用 Mockito 判断某些对象的方法是否被调用了

对 UI 方法的测试 (比如对 Activity 的方法进行测试)，由于测试时需要连接手机或模拟器，比较麻烦，暂时略过吧。使用的框架是 Google 官方提供的 Espresso。也有第三方提供的 Robolectric 框架，可以不需要设备，支持直接在 JVM 上跑，但应该也不简单。

因为对 UI 方法的测试是否设备相关的，所以测试的类名一般都以 `InstrumentedTest` 结尾。

所以对 Android 的测试，我们重点只关注对非 UI 方法的单元测试，重点掌握 JUnit 和 Mockito 的使用就行。

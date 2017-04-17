# Android Test Note

## References

1. [Android Testing Codelab](https://codelabs.developers.google.com/codelabs/android-testing/index.html)

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

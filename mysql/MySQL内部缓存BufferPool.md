## MySQL内部缓存BufferPool

* MySQL如何减少磁盘IO压力

![MySQL-BufferPool](https://cdn.jsdelivr.net/gh/ClareTung/ImageHostingService/img/MySQL-BufferPool.jpg)

### Buffer Pool

* MySQL做缓存，需要申请一块内存空间，这块内存空间称为Buffer Pool。
* 查询：从磁盘加载到缓存，后续直接查缓存
* 插入：直接写入缓存
* 更新删除：缓存中存在直接更新，不存在加载数据页到缓存更新

### 缓存页

* `MySQL`数据是以页为单位，每页默认`16KB`，称为数据页，在`Buffer Pool`里面会划分出若干**个缓存页**与数据页对应。

### 描述数据

* 描述数据与缓存页一一对应，包含一些所属表空间、数据页的编号、`Buffer Pool`中的地址等等
* 直接更新数据的缓存页称为**脏页**，缓存页刷盘后称为**干净页**

### Free链表

* 把空闲缓存页的**描述数据**放入链表中，这个链表称为`free`链表。目的是用来确定哪些缓存页是空闲的。

### 缓存哈希表

* 如何在`Buffer Pool`里快速定位到对应的缓存页呢？
* 使用哈希表将**表空间号+数据页号**，作为一个`key`，然后缓存页的地址作为`value`。
* 每次加载数据页到空闲缓存页时，就写入一条映射关系到**缓存页哈希表**中。

### Flush链表

* **空闲时会有异步线程做缓存页刷盘，保证数据的持久性与完整性**
* 如何确定哪些缓存页是脏页需要刷入磁盘。
* 只要缓存页被更新，就将它的**描述数据**加入`flush`链表
* 后续异步线程都从`flush`链表刷缓存页，当`Buffer Pool`内存不足时，也会优先刷`flush`链表里的缓存页。

### LRU链表

* `free`链表中的空闲缓存页会越来越少，直到没有，最后磁盘的数据页无法加载。为了解决这个问题，我们需要淘汰缓存页，腾出空闲缓存页。
* 为了处理**预读机制**和**全表扫描**
  * 预读机制是指`MySQL`加载数据页时，可能会把它相邻的数据页一并加载进来（局部性原理）
  * 全表扫描，如果**表数据量大**，大量的数据页会把空闲缓存页用完
* `LRU`链表做冷热数据分离设计，把`LRU`链表按一定比例，分为冷热区域，热区域称为`young`区域，冷区域称为`old`区域。
* **以7:3为例，young区域70%，old`区域30%**




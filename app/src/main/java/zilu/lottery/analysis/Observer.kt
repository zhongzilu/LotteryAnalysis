/*
 *
 *        ___                       ___       ___
 *       /\  \          ___        /\__\     /\__\
 *       \:\  \        /\  \      /:/  /    /:/  /
 *        \:\  \       \:\  \    /:/  /    /:/  /
 *         \:\  \      /::\__\  /:/  /    /:/  /  ___
 *   _______\:\__\  __/:/\/__/ /:/__/    /:/__/  /\__\
 *   \::::::::/__/ /\/:/  /    \:\  \    \:\  \ /:/  /
 *    \:\~~\~~     \::/__/      \:\  \    \:\  /:/  /
 *     \:\  \       \:\__\       \:\  \    \:\/:/  /
 *      \:\__\       \/__/        \:\__\    \::/  /
 *       \/__/                     \/__/     \/__/
 *
 *  Copyright (C) 2019 ZiLu https://github.com/zhongzilu
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package zilu.lottery.analysis

/**
 *
 * Create by zilu 2021/08/12
 */

/**
 * 观察者接口
 */
interface IObserver<E> {
    fun notify(e: E)
    fun onDone()
}

/**
 * 抽象观察者
 */
abstract class AbstractObserver<E> : IObserver<E> {

    fun watch(target: IObserveTarget<E>) {
        target.addObserver(this)
    }

    fun cancel(target: IObserveTarget<E>) {
        target.removeObserver(this)
    }

    override fun onDone() {}
}

/**
 * 观察目标接口，也就是被[IObserver]观察的目标对象
 */
interface IObserveTarget<T> {
    fun addObserver(observer: IObserver<T>)

    fun removeObserver(observer: IObserver<T>)
}

/**
 * 抽象观察目标对象(被观察地对象)，实现从集合中添加/移除观察者，同时添加了一个[notifyAll]方法，用于通知所有观察者.
 * 需要注意的时，在继承该类时需要在初始化时，对[observers]进行初始化赋值，不然将抛出空指针异常
 */
abstract class AbstractObserveTarget<T>(
    @JvmField
    protected val observers: MutableCollection<IObserver<T>>
) : IObserveTarget<T> {

    override fun addObserver(observer: IObserver<T>) {
        observers.add(observer)
    }

    override fun removeObserver(observer: IObserver<T>) {
        observers.remove(observer)
    }

    protected fun notifyAll(e: T) {
        observers.forEach { it.notify(e) }
    }

    open fun onDone() {
        observers.forEach { it.onDone() }
    }
}
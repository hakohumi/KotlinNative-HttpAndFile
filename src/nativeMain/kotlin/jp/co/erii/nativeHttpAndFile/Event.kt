package jp.co.erii.nativeHttpAndFile

typealias EventHandler<T> = (T, Int) -> Unit

class Event<T : Any> {
    private var handlers = emptyList<EventHandler<T>>()

    fun subscribe(handler: EventHandler<T>): EventHandler<T> {
        handlers = handlers + handler
        return handler
    }

    fun unsubscribe(handler: EventHandler<T>) {
        handlers = handlers - handler
    }

    fun unsubscribe(hash: Int) {
        handlers = handlers.filter { it.hashCode() != hash }
    }

    fun invoke(value: T) {
        var exception: Throwable? = null
        for (handler in handlers) {
            try {
                handler(value, handler.hashCode())
            } catch (e: Throwable) {
                exception = e
            }
        }
        exception?.let { throw it }
    }
}
package jp.co.erii.nativeHttpAndFile

typealias EventHandler<T> = (T) -> Unit

class Event<T : Any> {
    private var handlers = emptyList<EventHandler<T>>()

    fun subscribe(handler: EventHandler<T>): EventHandler<T> {
        handlers = handlers + handler
        return handler
    }

    fun unsubscribe(handler: EventHandler<T>) {
        handlers = handlers - handler
    }

    fun invoke(value: T) {
        var exception: Throwable? = null
        for (handler in handlers) {
            try {
                handler(value)
            } catch (e: Throwable) {
                exception = e
            }
        }
        exception?.let { throw it }
    }
}
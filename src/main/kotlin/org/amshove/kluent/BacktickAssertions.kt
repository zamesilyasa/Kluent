package org.amshove.kluent

import org.junit.Assert.*
import org.junit.ComparisonFailure
import kotlin.reflect.KClass

infix fun Any?.`should equal`(theOther: Any?) = assertEquals(theOther, this)
infix fun Any?.`should not equal`(theOther: Any?) = assertNotEquals(theOther, this)

infix fun <T> Array<T>?.`should equal`(theOther: Array<T>?) = assertArrayEquals(theOther, this)
infix fun <T> Iterable<T>?.`should equal`(theOther: Iterable<T>?) = assertEquals(theOther, this)

infix fun Any?.`should be`(theOther: Any?) = assertSame(theOther, this)
infix fun Any?.`should not be`(theOther: Any?) = assertNotSame(theOther, this)

infix fun <T> Array<T>.`should contain`(theThing: T) = if (this.contains(theThing)) Unit else fail("$this should contain $theThing", "$theThing", "${join(this)}")
infix fun <T> Array<T>.`should not contain`(theThing: T) = if (!this.contains(theThing)) Unit else fail("$this should not contain $theThing", "$theThing", "${join(this)}")

infix fun <T> Iterable<T>.`should contain`(theThing: T) = if (this.contains(theThing)) Unit else fail("$this should contain $theThing", "$theThing", "${join(this)}")
infix fun <T> Iterable<T>.`should not contain`(theThing: T) = if (!this.contains(theThing)) Unit else fail("$this should not contain $theThing", "$theThing", "${join(this)}")

infix fun <T> Any?.`should be in`(array: Array<T>) = if (array.contains(this)) Unit else fail("$this should be in $array", "$this", "${join(array)}")
infix fun <T> Any?.`should not be in`(array: Array<T>) = if (!array.contains(this)) Unit else fail("$this should not be in $array", "$this", "${join(array)}")

infix fun <T> Any?.`should be in`(iterable: Iterable<T>) = if (iterable.contains(this)) Unit else fail("$this should be in $iterable", "$this", "${join(iterable)}")
infix fun <T> Any?.`should not be in`(iterable: Iterable<T>) = if (!iterable.contains(this)) Unit else fail("$this should not be in $iterable", "$this", "${join(iterable)}")

infix fun <T : Exception> (() -> Unit).`should throw`(expectedException: KClass<T>) {
    try {
        this.invoke()
        fail("There was an Exception expected to be thrown, but nothing was thrown", "$expectedException", "None")
    } catch (e: Exception) {
        if (expectedException.isAnyException()) {
            return
        }
        if (e.javaClass !== expectedException.javaObjectType) {
            throw ComparisonFailure("Expected ${expectedException.javaObjectType} to be thrown", "${expectedException.javaObjectType}", "${e.javaClass}")
        }
    }
}

infix fun <T : Exception> (() -> Unit).`should throw the Exception`(expectedException: KClass<T>): ExceptionResult {
    try {
        this.invoke()
        fail("There was an Exception expected to be thrown, but nothing was thrown", "$expectedException", "None")
    } catch (e: Exception) {
        if (e.javaClass == expectedException.javaObjectType) {
            return ExceptionResult(e)
        } else throw ComparisonFailure("Expected ${expectedException.javaObjectType} to be thrown", "${expectedException.javaObjectType}", "${e.javaClass}")
    }
}

infix fun <T : Exception> (() -> Unit).`should not throw`(expectedException: KClass<T>) {
    try {
        this.invoke()
    } catch (e: Exception) {
        if (expectedException.isAnyException()) {
            fail("Expected no Exception to be thrown", "No Exception", "${e.javaClass}")
        }
        if (e.javaClass == expectedException.javaObjectType) {
            fail("Expected ${expectedException.javaObjectType} to not be thrown", "${e.javaClass}", "${expectedException.javaObjectType}")
        }
    }
}

infix fun <T : Exception> (() -> Unit).`should not throw the Exception`(expectedException: KClass<T>): NotThrowExceptionResult {
    try {
        this.invoke()
        return NotThrowExceptionResult(noException)
    } catch (e: Exception) {
        if (expectedException.isAnyException()) {
            fail("Expected no Exception to be thrown", "No Exception", "${e.javaClass}")
        }
        return NotThrowExceptionResult(e)
    }
}

infix fun ExceptionResult.`with message`(theMessage: String) {
    this.exceptionMessage `should equal` theMessage
}

infix fun NotThrowExceptionResult.`with message`(theMessage: String) {
    this.exceptionMessage `should not equal` theMessage
}

val AnyException = AnyExceptionType::class

class AnyExceptionType : Exception() {}

private val noException = Exception("None")
private fun <T : Exception> KClass<T>.isAnyException() = this.javaObjectType == AnyException.javaObjectType
private fun fail(message: String, expected: String, actual: String): Nothing = throw ComparisonFailure(message, expected, actual)
private fun <T> join(theArray: Array<T>): String = theArray.joinToString(", ")
private fun <T> join(theIterable: Iterable<T>): String = theIterable.joinToString(", ")
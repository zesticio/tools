package com.zestic.core.exceptions;

import com.zestic.core.io.FastByteArrayOutputStream;
import com.zestic.core.map.MapUtil;
import com.zestic.core.util.ArrayUtil;
import com.zestic.core.util.ReflectUtil;
import com.zestic.core.util.StrUtil;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExceptionUtil {

    public static String getMessage(Throwable e) {
        if (null == e) {
            return StrUtil.NULL;
        }
        return StrUtil.format("{}: {}", e.getClass().getSimpleName(), e.getMessage());
    }

    public static String getSimpleMessage(Throwable e) {
        return (null == e) ? StrUtil.NULL : e.getMessage();
    }

    public static RuntimeException wrapRuntime(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            return (RuntimeException) throwable;
        }
        return new RuntimeException(throwable);
    }

    public static RuntimeException wrapRuntime(String message) {
        return new RuntimeException(message);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T wrap(Throwable throwable, Class<T> wrapThrowable) {
        if (wrapThrowable.isInstance(throwable)) {
            return (T) throwable;
        }
        return ReflectUtil.newInstance(wrapThrowable, throwable);
    }

    public static void wrapAndThrow(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException) throwable;
        }
        if (throwable instanceof Error) {
            throw (Error) throwable;
        }
        throw new UndeclaredThrowableException(throwable);
    }

    public static void wrapRuntimeAndThrow(String message) {
        throw new RuntimeException(message);
    }

    public static Throwable unwrap(Throwable wrapped) {
        Throwable unwrapped = wrapped;
        while (true) {
            if (unwrapped instanceof InvocationTargetException) {
                unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
            } else if (unwrapped instanceof UndeclaredThrowableException) {
                unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
            } else {
                return unwrapped;
            }
        }
    }

    public static StackTraceElement[] getStackElements() {
        // return (new Throwable()).getStackTrace();
        return Thread.currentThread().getStackTrace();
    }

    public static StackTraceElement getStackElement(int i) {
        return Thread.currentThread().getStackTrace()[i];
    }

    public static StackTraceElement getStackElement(String fqcn, int i) {
        final StackTraceElement[] stackTraceArray = Thread.currentThread().getStackTrace();
        final int index = ArrayUtil.matchIndex((ele) -> StrUtil.equals(fqcn, ele.getClassName()),
                stackTraceArray);
        if (index > 0) {
            return stackTraceArray[index + i];
        }
        return null;
    }

    public static StackTraceElement getRootStackElement() {
        final StackTraceElement[] stackElements = Thread.currentThread().getStackTrace();
        return Thread.currentThread().getStackTrace()[stackElements.length - 1];
    }

    public static String stacktraceToOneLineString(Throwable throwable) {
        return stacktraceToOneLineString(throwable, 3000);
    }

    public static String stacktraceToOneLineString(Throwable throwable, int limit) {
        Map<Character, String> replaceCharToStrMap = new HashMap<>();
        replaceCharToStrMap.put(StrUtil.C_CR, StrUtil.SPACE);
        replaceCharToStrMap.put(StrUtil.C_LF, StrUtil.SPACE);
        replaceCharToStrMap.put(StrUtil.C_TAB, StrUtil.SPACE);
        return stacktraceToString(throwable, limit, replaceCharToStrMap);
    }

    public static String stacktraceToString(Throwable throwable) {
        return stacktraceToString(throwable, 3000);
    }

    public static String stacktraceToString(Throwable throwable, int limit) {
        return stacktraceToString(throwable, limit, null);
    }

    public static String stacktraceToString(Throwable throwable, int limit,
                                            Map<Character, String> replaceCharToStrMap) {
        final FastByteArrayOutputStream baos = new FastByteArrayOutputStream();
        throwable.printStackTrace(new PrintStream(baos));

        final String exceptionStr = baos.toString();
        final int length = exceptionStr.length();
        if (limit < 0 || limit > length) {
            limit = length;
        }

        if (MapUtil.isNotEmpty(replaceCharToStrMap)) {
            final StringBuilder sb = StrUtil.builder();
            char c;
            String value;
            for (int i = 0; i < limit; i++) {
                c = exceptionStr.charAt(i);
                value = replaceCharToStrMap.get(c);
                if (null != value) {
                    sb.append(value);
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        } else {
            if (limit == length) {
                return exceptionStr;
            }
            return StrUtil.subPre(exceptionStr, limit);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean isCausedBy(Throwable throwable,
                                     Class<? extends Exception>... causeClasses) {
        return null != getCausedBy(throwable, causeClasses);
    }

    @SuppressWarnings("unchecked")
    public static Throwable getCausedBy(Throwable throwable,
                                        Class<? extends Exception>... causeClasses) {
        Throwable cause = throwable;
        while (cause != null) {
            for (Class<? extends Exception> causeClass : causeClasses) {
                if (causeClass.isInstance(cause)) {
                    return cause;
                }
            }
            cause = cause.getCause();
        }
        return null;
    }

    public static boolean isFromOrSuppressedThrowable(Throwable throwable,
                                                      Class<? extends Throwable> exceptionClass) {
        return convertFromOrSuppressedThrowable(throwable, exceptionClass, true) != null;
    }

    public static boolean isFromOrSuppressedThrowable(Throwable throwable,
                                                      Class<? extends Throwable> exceptionClass, boolean checkCause) {
        return convertFromOrSuppressedThrowable(throwable, exceptionClass, checkCause) != null;
    }

    public static <T extends Throwable> T convertFromOrSuppressedThrowable(Throwable throwable,
                                                                           Class<T> exceptionClass) {
        return convertFromOrSuppressedThrowable(throwable, exceptionClass, true);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T convertFromOrSuppressedThrowable(Throwable throwable,
                                                                           Class<T> exceptionClass, boolean checkCause) {
        if (throwable == null || exceptionClass == null) {
            return null;
        }
        if (exceptionClass.isAssignableFrom(throwable.getClass())) {
            return (T) throwable;
        }
        if (checkCause) {
            Throwable cause = throwable.getCause();
            if (cause != null && exceptionClass.isAssignableFrom(cause.getClass())) {
                return (T) cause;
            }
        }
        Throwable[] throwables = throwable.getSuppressed();
        if (ArrayUtil.isNotEmpty(throwables)) {
            for (Throwable throwable1 : throwables) {
                if (exceptionClass.isAssignableFrom(throwable1.getClass())) {
                    return (T) throwable1;
                }
            }
        }
        return null;
    }

    public static List<Throwable> getThrowableList(Throwable throwable) {
        final List<Throwable> list = new ArrayList<>();
        while (throwable != null && false == list.contains(throwable)) {
            list.add(throwable);
            throwable = throwable.getCause();
        }
        return list;
    }

    public static Throwable getRootCause(final Throwable throwable) {
        final List<Throwable> list = getThrowableList(throwable);
        return list.size() < 1 ? null : list.get(list.size() - 1);
    }

    public static String getRootCauseMessage(final Throwable th) {
        return getMessage(getRootCause(th));
    }
}

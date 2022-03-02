package com.zestic.common;

import com.zestic.common.inspection.InstanceCollector;

public class InstanceCollectorTest {

    /**
     * Test method for {@link InstanceCollector#instances()}.
     */
//    @Test
//    public void testInstances() {
//        InstanceCollector<TestInterface> instanceCollector = new InstanceCollector<>(TestInterface.class, TestInterface.class.getPackage().getName());
//        assertThat(instanceCollector.instances(), size(5));
//        assertThat(Collections2.transform(instanceCollector.instances(), toName()), containsAll("EINS", "ZWEI", "ABC", "DEF", "TestClass"));
//    }

//    private static Function<TestInterface, String> toName() {
//        return new Function<TestInterface, String>() {
//            @Override
//            public String apply(TestInterface input) {
//                return input.name();
//            }
//        };
//    }

//    private static TypeSafeMatcher<Collection<?>> size(final int size) {
//        return new TypeSafeMatcher<Collection<?>>() {
//
//            @Override
//            public void describeTo(Description description) {
//                description.appendText("Collection of size " + size + ".");
//            }
//
//            @Override
//            protected boolean matchesSafely(Collection<?> item) {
//                return item.size() == size;
//            }
//        };
//    }

//    private static TypeSafeMatcher<Collection<String>> containsAll(final String... strings) {
//        return new TypeSafeMatcher<Collection<String>>() {
//
//            @Override
//            public void describeTo(Description description) {
//                description.appendText("Collection containing all strings (" + Arrays.toString(strings) + ")");
//
//            }
//
//            @Override
//            protected boolean matchesSafely(Collection<String> item) {
//                for (String s : strings) {
//                    if (!item.contains(s)) {
//                        return false;
//                    }
//                }
//                return true;
//            }
//        };
//    }
}

package com.zestic.common;

/**
 * @author hoersch
 */
public class ClassInspectorTest {

    // TODO tests mit Methoden aus basisklasse und überschrieben

    /**
     *
     */
//    @Test
//    public void testFindClassesAssignableFromObject() {
//        Collection<Class<?>> classes = ClassInspectionUtil.findClassesAssignableFrom(Object.class, this.getClass().getPackage().getName() + ".testpackage");
//
//        System.out.println(classes);
//        System.out.println(classes.size());
//
//        assertEquals("Number of found classes", 3, classes.size());
//        assertTrue("Contains PackagedAnnotatedAndMethodAnnotatedTestClass.class", classes.contains(PackagedAnnotatedAndMethodAnnotatedTestClass.class));
//        assertTrue("Contains PackagedAnnotatedTestClass.class", classes.contains(PackagedAnnotatedTestClass.class));
//        assertTrue("Contains PackagedNotAnnotatedTestClass.class", classes.contains(PackagedNotAnnotatedTestClass.class));
//    }

    /**
     *
     */
//    @Test
//    public void testFindClassesAssignableFromBaseClass() {
//        Collection<Class<? extends BaseClass>> classes = ClassInspectionUtil.findClassesAssignableFrom(BaseClass.class, this.getClass().getPackage().getName() + ".testpackage_with_classhierarchy");
//        // Collection<Class<?>> classes =
//        // ClassInspectionUtil.findClassesAssignableFrom(BaseClass.class,
//        // "de.his");
//
//        System.out.println(classes);
//        System.out.println(classes.size());
//
//        assertEquals("Number of found classes", 3, classes.size());
//        assertTrue("Contains BaseClass.class", classes.contains(BaseClass.class));
//        assertTrue("Contains BaseClassExtendingClass1.class", classes.contains(BaseClassExtendingClass1.class));
//        assertTrue("Contains BaseClassExtendingClass2.class", classes.contains(BaseClassExtendingClass2.class));
//    }

    /**
     *
     */
//    @Test
//    public void testFindClassesAssignableFromSuperBaseClass() {
//        // Collection<Class<?>> classes =
//        // ClassInspectionUtil.findClassesAssignableFrom(SuperBaseClass.class,
//        // this.getClass().getPackage().getName() +
//        // ".testpackage_with_classhierarchy");
//        Collection<Class<? extends SuperBaseClass>> classes = ClassInspectionUtil.findClassesAssignableFrom(SuperBaseClass.class, "de.dennishoersch.util");
//
//        System.out.println(classes);
//        System.out.println(classes.size());
//
//        assertEquals("Number of found classes", 7, classes.size());
//        assertTrue("Contains SuperBaseClass.class", classes.contains(SuperBaseClass.class));
//        assertTrue("Contains BaseClass.class", classes.contains(BaseClass.class));
//        assertTrue("Contains BaseClassExtendingClass1.class", classes.contains(BaseClassExtendingClass1.class));
//        assertTrue("Contains BaseClassExtendingClass2.class", classes.contains(BaseClassExtendingClass2.class));
//        assertTrue("Contains SuperBaseClassExtendingClass.class", classes.contains(SuperBaseClassExtendingClass.class));
//        assertTrue("Contains ClassWithInnerClassExtendingFromSuperBaseClass.InnerClass.class", classes.contains(ClassWithInnerClassExtendingFromSuperBaseClass.InnerClass.class));
//        assertTrue("Contains ClassWithInnerClassExtendingFromSuperBaseClass$PrivateInnerClass.class", Iterables.contains(Iterables.transform(classes, ClassInspectionUtil.classToName()),
//                "de.dennishoersch.util.inspection.testpackage_with_classhierarchy.ClassWithInnerClassExtendingFromSuperBaseClass$PrivateInnerClass"));
//    }

    /**
     *
     */
//    @Test
//    public void testFindClassesAssignableFromTestInterface() {
//        Collection<Class<? extends TestInterface>> classes = ClassInspectionUtil.findClassesImplementing(TestInterface.class, TestInterface.class.getPackage().getName());
//
//        System.out.println(classes);
//        System.out.println(classes.size());
//
//        assertEquals("Number of found classes", 4, classes.size());
//        assertTrue("Contains TestClass1.class", classes.contains(TestClass1.class));
//        assertTrue("Contains TestClass2.class", classes.contains(TestClass2.class));
//        assertTrue("Contains SubOfTestClass2.class", classes.contains(SubOfTestClass2.class));
//        assertTrue("Contains TestEnum.class", classes.contains(TestEnum.class));
//    }

    /**
     *
     */
//    @Test
//    public void testFindAnnotated() {
//        Collection<Class<?>> classes = ClassInspectionUtil.findAnnotatedClasses(ClassInspectorTestAnnotationOnlyOnType.class, this.getClass().getPackage().getName());
//        // Collection<Class<?>> classes =
//        // ClassInspectionUtil.findAnnotatedClasses(ClassInspectorTestAnnotationOnlyOnType.class,
//        // "de.his");
//
//        System.out.println(classes);
//        assertEquals("Number of found classes", 2, classes.size());
//
//        assertTrue("Contains AnnotatedTestClass.class", classes.contains(de.dennishoersch.util.inspection.AnnotatedTestClass.class));
//        assertTrue("Contains PackagedAnnotatedTestClass.class", classes.contains(PackagedAnnotatedTestClass.class));
//    }

    /**
     *
     */
//    @Test
//    public void testFindAnnotatedElementsOnClassAndMethod() {
//        Collection<ClassAnnotationMetadata> classes = ClassInspectionUtil.findAnnotatedElements(de.dennishoersch.util.inspection.ClassInspectorTestAnnotationOnlyOnTypeAndMethod.class, this.getClass().getPackage().getName());
//        // Collection<ClassMetadata> classes =
//        // ClassInspectionUtil.findAnnotatedElements(ClassInspectorTestAnnotationOnlyOnTypeAndMethod.class,
//        // "de.his");
//
//        System.out.println(classes);
//        assertEquals("Number of found classes", 1, classes.size());
//        ClassAnnotationMetadata classMetadata = classes.iterator().next();
//        assertTrue("Class is annotated", classMetadata.isRelatedClassAnnotated());
//        assertTrue("Class is PackagedAnnotatedAndMethodAnnotatedTestClass.class", classMetadata.getRelatedClass().equals(PackagedAnnotatedAndMethodAnnotatedTestClass.class));
//        assertEquals("Number of annotated methods", 1, classMetadata.getAnnotatedMethods().size());
//        assertEquals("Number of annotated fields", 0, classMetadata.getAnnotatedFields().size());
//
//    }

    /**
     *
     */
//    @Test
//    public void testFindAnnotatedElementsOnClass() {
//        Collection<ClassAnnotationMetadata> classes = ClassInspectionUtil.findAnnotatedElements(ClassInspectorTestAnnotationOnlyOnType.class, this.getClass().getPackage().getName());
//        System.out.println(classes);
//        assertEquals("Number of found classes", 2, classes.size());
//        for (ClassAnnotationMetadata classMetadata : classes) {
//            assertEquals("Number of annotated methods", 0, classMetadata.getAnnotatedMethods().size());
//            assertEquals("Number of annotated fields", 0, classMetadata.getAnnotatedMethods().size());
//        }
//    }
}

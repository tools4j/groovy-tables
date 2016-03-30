package org.tools4j.groovytabledsl

import java.util.function.Predicate

/**
 * User: ben
 * Date: 7/03/2016
 * Time: 5:50 PM
 */
class ConstructionMethodFilter implements Predicate<ConstructionMethod> {
    private final List<Closure<Boolean>> predicates
    final static ConstructionMethodFilter CONSTRUCTORS = filter().withConstructors().toUnmodifiable()
    final static ConstructionMethodFilter STATIC_FACTORY_METHODS = filter().withStaticFactoryMethods().toUnmodifiable()
    final static ConstructionMethodFilter REFLECTION = filter().withReflection().toUnmodifiable()
    final static ConstructionMethodFilter INCLUDE_ALL = filter().toUnmodifiable()

    static ConstructionMethodFilter filter(){
        return new ConstructionMethodFilter();
    }

    private ConstructionMethodFilter() {
        this(new ArrayList<Closure<Boolean>>())
    }

    private ConstructionMethodFilter(final List<Closure<Boolean>> predicates) {
        this.predicates = predicates
    }

    public Collection<ConstructionMethod> apply(Collection<ConstructionMethod> collection){
        return collection.findAll{this.test(it)}
    }

    ConstructionMethodFilter toUnmodifiable(){
        return new ConstructionMethodFilter(Collections.unmodifiableList(this.predicates))
    }

    ConstructionMethodFilter withConstructors(){
        with{it instanceof ClassConstructor}
        return this
    }

    ConstructionMethodFilter withStaticFactoryMethods(){
        with{it instanceof StaticFactoryMethod}
        return this
    }

    ConstructionMethodFilter withReflection(){
        with{it instanceof ReflectionConstructionMethod}
        return this
    }

    ConstructionMethodFilter withName(final String name){
        with{it instanceof ExecutableConstructionMethod && it.executable.name == name}
        return this
    }

    ConstructionMethodFilter with(final Closure<Boolean> predicate){
        predicates.add(predicate)
        return this;
    }

    @Override
    boolean test(final ConstructionMethod constructionMethod) {
        for(final Closure<Boolean> predicate: predicates){
            if(!predicate(constructionMethod)){
                return false;
            }
        }
        return true;
    }
}

package com.github.alexandrenavarro.analyticalquery4j;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.meanbean.test.BeanVerifier;
import org.meanbean.test.VerifierSettings;

import lombok.Setter;
import nl.jqno.equalsverifier.EqualsVerifier;

public abstract class AbstractPojoTest<T, B> {

  public abstract T getFakeInstance();

  public abstract B getBuilder();

  // You can override if needed
  @Setter
  protected Consumer<VerifierSettings> verifierSettingsConsumer =
    (VerifierSettings settings) -> settings.registerFactory(
      (Class<T>) getFakeInstance().getClass(), () -> getFakeInstance());

  @Test
  // Can be override bye if
  // absolutelyNeededEqualsVerifier.simple().forClass(getFakeInstance().getClass()).verify();
  protected void shouldEqualsWorks() {
    EqualsVerifier.forClass(getFakeInstance().getClass()).verify();
  }

  @Test
  void shouldHaveToString() {
    final T fakeInstance = getFakeInstance();
    assertThat(fakeInstance.toString())
      // does not have the default implementation
      .doesNotContain(fakeInstance.getClass() + "@")
      // but have at least lombok implementation
      .startsWith(
        fakeInstance
          .getClass()
          .getName()
          .substring(fakeInstance.getClass().getName().lastIndexOf(".") + 1)
          .replace('$', '.'));
  }

  @Test
  void shouldHaveCorrectBuilderToString() {
    final B builder = getBuilder();
    assertThat(builder.toString()).isNotEmpty();
  }

  @Test
  void shouldSetterGetterWorks() {
    BeanVerifier.forClass((Class<T>) getFakeInstance().getClass())
      .withSettings(verifierSettingsConsumer)
      .verifyGettersAndSetters();
  }

  @Test
  void shouldBeAbleToCallEmptyConstructorWhenItExists()
    throws InvocationTargetException, InstantiationException, IllegalAccessException {
    try {
      final Constructor<?> declaredConstructor =
        getFakeInstance().getClass().getDeclaredConstructor();
      final Object o = declaredConstructor.newInstance();
      assertThat(o).isNotNull();
    } catch (NoSuchMethodException e) {
      // Accept if does not exists.
    }
  }
}

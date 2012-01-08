/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dk.clanie.actor;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.ProxyConfig;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ClassUtils;

/**
 * Bean post-processor that automatically applies asynchronous invocation
 * behavior to any bean that carries the {@link Actor} annotation by adding
 * a corresponding {@link ActorAnnotationAdvisor} to the
 * exposed proxy (either an existing AOP proxy or a newly generated proxy that
 * implements all of the target's interfaces).
 * <p/>
 * Based on Spring 3.0's AsyncAnnotationBeanPostProcessor.
 * 
 * @author Claus Nielsen
 * @see Actor
 * @see ActorAnnotationAdvisor
 */
@SuppressWarnings("serial")
public class ActorAnnotationBeanPostProcessor extends ProxyConfig
		implements BeanPostProcessor, BeanClassLoaderAware, InitializingBean, Ordered {

	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

	/**
	 * This should run after all other post-processors, so that it can just add
	 * an advisor to existing proxies rather than double-proxy.
	 */
	private int order = Ordered.LOWEST_PRECEDENCE;


	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}

	public void afterPropertiesSet() {
	}

	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}


	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		return bean;
	}

	public Object postProcessAfterInitialization(Object bean, String beanName) {
		if (bean instanceof AopInfrastructureBean) {
			// Ignore AOP infrastructure such as scoped proxies.
			return bean;
		}
		Class<?> targetClass = AopUtils.getTargetClass(bean);
		Actor annotation = AnnotationUtils.findAnnotation(targetClass, Actor.class);
		if (annotation != null) {
//		if (AopUtils.canApply(this.asyncAnnotationAdvisor, targetClass)) {

			ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
			executor.setMaxPoolSize(1);
			executor.setDaemon(true);
			String threadNamePrefix = beanName + ",";
			executor.setThreadNamePrefix(threadNamePrefix);
			executor.initialize();

			ActorAnnotationAdvisor actorAnnotationAdvisor = new ActorAnnotationAdvisor(executor);

			if (bean instanceof Advised) {
				((Advised) bean).addAdvisor(0, actorAnnotationAdvisor);
				return bean;
			} else {
				ProxyFactory proxyFactory = new ProxyFactory(bean);
				// Copy our properties (proxyTargetClass etc) inherited from ProxyConfig.
				proxyFactory.copyFrom(this);
				proxyFactory.addAdvisor(actorAnnotationAdvisor);
				return proxyFactory.getProxy(this.beanClassLoader);
			}
		}
		else {
			// No async proxy needed.
			return bean;
		}
	}

}

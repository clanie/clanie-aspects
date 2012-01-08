/*
 * Copyright 2002-2009 the original author or authors.
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

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.core.task.AsyncTaskExecutor;

/**
 * Advisor that activates asynchronous method execution through the {@link Actor}
 * annotation. This annotation can be used at type level in implementation
 * classes as well as in service interfaces.
 * <p/>
 * Based on Spring 3.0's AsyncAnnotationAdvisor.
 * 
 * @author Claus Nielsen
 */
@SuppressWarnings("serial")
public class ActorAnnotationAdvisor extends AbstractPointcutAdvisor {

	private Advice advice;

	private Pointcut pointcut;


	/**
	 * Create a new ActorAnnotationAdvisor using the given task executor.
	 * 
	 * @param executor the task executor to use for asynchronous methods
	 */
	public ActorAnnotationAdvisor(Executor executor) {
		Set<Class<? extends Annotation>> asyncAnnotationTypes = new LinkedHashSet<Class<? extends Annotation>>(2);
		asyncAnnotationTypes.add(Actor.class);
		this.advice = buildAdvice(executor);
		this.pointcut = new AnnotationMatchingPointcut(Actor.class, true);
	}


	public Advice getAdvice() {
		return this.advice;
	}


	public Pointcut getPointcut() {
		return this.pointcut;
	}


	protected Advice buildAdvice(Executor executor) {
		if (executor instanceof AsyncTaskExecutor) {
			return new ActorExecutionInterceptor((AsyncTaskExecutor) executor);
		}
		else {
			return new ActorExecutionInterceptor(executor);
		}
	}


}

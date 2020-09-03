package com.docomodigital.exerciseapi;

import java.util.function.Supplier;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.docomodigital.exerciseapi.common.exception.ApiException;

public class ApiExceptionMatcher extends TypeSafeMatcher<ApiException> {

	public static ApiExceptionMatcher is(Supplier<ApiException> item) {
		return new ApiExceptionMatcher(item.get());
	}

	private ApiException foundException;
	private final ApiException expectedException;

	private ApiExceptionMatcher(ApiException expectedCrdException) {
		this.expectedException = expectedCrdException;
	}

	@Override
	public boolean matchesSafely(final ApiException exception) {
		foundException = exception;
		return expectedException.equals(foundException);
	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(foundException).appendText(" was found. expected was: ")
				.appendValue(expectedException);
	}

}

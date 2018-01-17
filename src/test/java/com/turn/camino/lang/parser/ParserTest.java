/*
 * Copyright (C) 2014-2018, Amobee Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */
package com.turn.camino.lang.parser;

import com.turn.camino.lang.ast.*;

import java.io.StringReader;
import java.util.List;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Unit test for Parser
 *
 * @author llo
 */
@Test
public class ParserTest {

	/**
	 * Test parsing a block
	 *
	 * @throws ParseException
	 */
	@Test
	public void testBlock() throws ParseException {
		Block block = parser("foo <%=666%> bar").block();
		assertEquals(block.getLocation().getColumn(), 1);
		assertEquals(block.getLocation().getLine(), 1);
		assertEquals(block.getExpressions().size(), 3);
		assertEquals(block.getExpressions().get(0).getClass(), StringLiteral.class);
		assertEquals(((StringLiteral) block.getExpressions().get(0)).getValue(), "foo ");
		assertEquals(block.getExpressions().get(1).getClass(), LongLiteral.class);
		assertEquals(((LongLiteral) block.getExpressions().get(1)).longValue(), 666L);
		assertEquals(block.getExpressions().get(2).getClass(), StringLiteral.class);
		assertEquals(((StringLiteral) block.getExpressions().get(2)).getValue(), " bar");
	}

	/**
	 * Test parsing number literal
	 *
	 * @throws ParseException
	 */
	@Test
	public void testNumberLiteral() throws ParseException {

		// test long literal
		NumberLiteral numberLiteral = langParser("123").numberLiteral();
		assertEquals(numberLiteral.getClass(), LongLiteral.class);
		assertEquals(numberLiteral.longValue(), 123L);

		// test negative long literal
		numberLiteral = langParser("-987").numberLiteral();
		assertEquals(numberLiteral.getClass(), LongLiteral.class);
		assertEquals(numberLiteral.longValue(), -987L);

		// test double literal
		numberLiteral = langParser("123.45").numberLiteral();
		assertEquals(numberLiteral.getClass(), DoubleLiteral.class);
		assertEquals(numberLiteral.doubleValue(), 123.45, 1e-6);

		// test negative double literal
		numberLiteral = langParser("-987.65").numberLiteral();
		assertEquals(numberLiteral.getClass(), DoubleLiteral.class);
		assertEquals(numberLiteral.doubleValue(), -987.65, 1e-6);

		// test double literal in scientific notation
		numberLiteral = langParser("123.45e6").numberLiteral();
		assertEquals(numberLiteral.getClass(), DoubleLiteral.class);
		assertEquals(numberLiteral.doubleValue(), 1.2345e8, 1e-6);
	}

	/**
	 * Test parsing string literal
	 *
	 * @throws ParseException
	 */
	@Test
	public void testStringLiteral() throws ParseException {

		// non-empty string
		StringLiteral stringLiteral = langParser("'abc'").stringLiteral();
		assertEquals(stringLiteral.getValue(), "abc");

		// empty string
		stringLiteral = langParser("''").stringLiteral();
		assertEquals(stringLiteral.getValue(), "");
	}

	/**
	 * Test parsing an identifier
	 *
	 * @throws ParseException
	 */
	@Test
	public void testIdentifier() throws ParseException {
		Identifier identifier = langParser("foo").identifier();
		assertEquals(identifier.getName(), "foo");
	}

	/**
	 * Test parsing a function
	 *
	 * @throws ParseException
	 */
	@Test
	public void testFunctionCall() throws ParseException {

		// function call with one argument
		Expression expression = langParser("today('GMT')").expression();
		assertEquals(expression.getClass(), FunctionCall.class);
		FunctionCall functionCall = (FunctionCall) expression;
		assertEquals(functionCall.getFunctionValue().getClass(), Identifier.class);
		assertEquals(((Identifier) functionCall.getFunctionValue()).getName(), "today");
		assertEquals(functionCall.getArguments().size(), 1);
		assertEquals(functionCall.getArguments().get(0).getClass(), StringLiteral.class);
		assertEquals(((StringLiteral) functionCall.getArguments().get(0)).getValue(), "GMT");

		// function call with no argument
		expression = langParser("now()").expression();
		assertEquals(expression.getClass(), FunctionCall.class);
		functionCall = (FunctionCall) expression;
		assertEquals(functionCall.getFunctionValue().getClass(), Identifier.class);
		assertEquals(((Identifier) functionCall.getFunctionValue()).getName(), "now");
		assertEquals(functionCall.getArguments().size(), 0);

		// function call with an expression as function value
		expression = langParser("a(1)(3)").expression();
		assertEquals(expression.getClass(), FunctionCall.class);
		functionCall = (FunctionCall) expression;
		assertEquals(functionCall.getFunctionValue().getClass(), FunctionCall.class);
		assertEquals(functionCall.getArguments().size(), 1);

		// function call with lambda expression
		expression = langParser("(fn(a,b)->mul(add(a,1),b))(4,5)").expression();
		assertEquals(expression.getClass(), FunctionCall.class);
		functionCall = (FunctionCall) expression;
		assertEquals(functionCall.getFunctionValue().getClass(), FunctionLiteral.class);
		assertEquals(functionCall.getArguments().size(), 2);
	}

	/**
	 * Test member access
	 *
	 * @throws ParseException
	 */
	@Test
	public void testMemberAccess() throws ParseException {

		// test accessing top level property
		Expression reference = langParser("foo").expression();
		assertEquals(reference.getClass(), Identifier.class);
		assertEquals(((Identifier) reference).getName(), "foo");

		// test running top level function
		reference = langParser("foo()").expression();
		assertEquals(reference.getClass(), FunctionCall.class);
		assertEquals(((FunctionCall) reference).getFunctionValue().getClass(), Identifier.class);
		assertEquals(((Identifier)((FunctionCall) reference).getFunctionValue()).getName(), "foo");

		// test accessing member field of an object
		reference = langParser("foo.bar").expression();
		assertEquals(reference.getClass(), MemberAccess.class);
		MemberAccess memberAccess = (MemberAccess) reference;
		assertNotNull(memberAccess.getParent());
		assertEquals(memberAccess.getParent().getClass(), Identifier.class);
		assertEquals(((Identifier) memberAccess.getParent()).getName(), "foo");
		assertNotNull(memberAccess.getChild());
		assertEquals(memberAccess.getChild().getName(), "bar");

		// test running member function of an object
		reference = langParser("foo.bar()").expression();
		assertEquals(reference.getClass(), FunctionCall.class);
		FunctionCall functionCall = (FunctionCall) reference;
		assertEquals(functionCall.getFunctionValue().getClass(), MemberAccess.class);
		memberAccess = (MemberAccess) functionCall.getFunctionValue();
		assertNotNull(memberAccess.getParent());
		assertEquals(memberAccess.getParent().getClass(), Identifier.class);
		assertEquals(((Identifier) memberAccess.getParent()).getName(), "foo");
		assertNotNull(memberAccess.getChild());
		assertEquals(memberAccess.getChild().getName(), "bar");

		// test running member function of object returned by running top-level function
		reference = langParser("foo().bar()").expression();
		assertEquals(reference.getClass(), FunctionCall.class);
		functionCall = (FunctionCall) reference;
		assertEquals(functionCall.getFunctionValue().getClass(), MemberAccess.class);
		memberAccess = (MemberAccess) functionCall.getFunctionValue();
		assertNotNull(memberAccess.getParent());
		assertEquals(memberAccess.getParent().getClass(), FunctionCall.class);
		assertEquals(((FunctionCall) memberAccess.getParent()).getFunctionValue().getClass(), Identifier.class);
		assertEquals(((Identifier)((FunctionCall) memberAccess.getParent()).getFunctionValue()).getName(), "foo");
		assertNotNull(memberAccess.getChild());
		assertEquals(memberAccess.getChild().getName(), "bar");

		// test accessing member field of object returned by running top-level function
		reference = langParser("foo().bar").expression();
		assertEquals(reference.getClass(), MemberAccess.class);
		memberAccess = (MemberAccess) reference;
		assertNotNull(memberAccess.getParent());
		assertEquals(memberAccess.getParent().getClass(), FunctionCall.class);
		assertEquals(((FunctionCall) memberAccess.getParent()).getFunctionValue().getClass(), Identifier.class);
		assertEquals(((Identifier)((FunctionCall) memberAccess.getParent()).getFunctionValue()).getName(), "foo");
		assertNotNull(memberAccess.getChild());
		assertEquals(memberAccess.getChild().getName(), "bar");
	}

	/**
	 * Test parsing if() operator
	 *
	 * @throws ParseException
	 */
	@Test
	public void testTernaryIf() throws ParseException {
		TernaryIf ternaryIf = langParser("if(t,1,-1)").ternaryIf();
		assertEquals(ternaryIf.getCondition().getClass(), Identifier.class);
		assertEquals(((Identifier) ternaryIf.getCondition()).getName(), "t");
		assertEquals(ternaryIf.getThenValue().getClass(), LongLiteral.class);
		assertEquals(((LongLiteral) ternaryIf.getThenValue()).longValue(), 1);
		assertEquals(ternaryIf.getElseValue().getClass(), LongLiteral.class);
		assertEquals(((LongLiteral) ternaryIf.getElseValue()).longValue(), -1);
	}

	/**
	 * Test a dictionary literal
	 *
	 * @throws ParseException
	 */
	@Test
	public void testDictionaryLiteral() throws ParseException {
		DictionaryLiteral dictionaryLiteral = langParser("{}").dictionaryLiteral();
		assertEquals(dictionaryLiteral.getEntries().size(), 0);
		dictionaryLiteral = langParser("{a:1}").dictionaryLiteral();
		assertEquals(dictionaryLiteral.getEntries().size(), 1);
		assertEquals(dictionaryLiteral.getEntries().get(0).getKey().getClass(), Identifier.class);
		assertEquals(((Identifier) dictionaryLiteral.getEntries().get(0).getKey()).getName(), "a");
		assertEquals(dictionaryLiteral.getEntries().get(0).getValue().getClass(), LongLiteral.class);
		assertEquals(((LongLiteral) dictionaryLiteral.getEntries().get(0).getValue()).longValue(), 1L);
		dictionaryLiteral = langParser("{'xyz':'stu',foo():{}}").dictionaryLiteral();
		assertEquals(dictionaryLiteral.getEntries().size(), 2);
		assertEquals(dictionaryLiteral.getEntries().get(0).getKey().getClass(), StringLiteral.class);
		assertEquals(((StringLiteral) dictionaryLiteral.getEntries().get(0).getKey()).getValue(), "xyz");
		assertEquals(dictionaryLiteral.getEntries().get(0).getValue().getClass(), StringLiteral.class);
		assertEquals(((StringLiteral) dictionaryLiteral.getEntries().get(0).getValue()).getValue(), "stu");
		assertEquals(dictionaryLiteral.getEntries().get(1).getKey().getClass(), FunctionCall.class);
		assertEquals(((FunctionCall) dictionaryLiteral.getEntries().get(1).getKey())
				.getFunctionValue().getClass(), Identifier.class);
		assertEquals(((Identifier) ((FunctionCall) dictionaryLiteral.getEntries().get(1).getKey())
				.getFunctionValue()).getName(), "foo");
		assertEquals(dictionaryLiteral.getEntries().get(1).getValue().getClass(), DictionaryLiteral.class);
		assertEquals(((DictionaryLiteral) dictionaryLiteral.getEntries().get(1).getValue()).getEntries().size(), 0);
	}

	/**
	 * Test a list literal
	 *
	 * @throws ParseException
	 */
	@Test
	public void testListLiteral() throws ParseException {
		ListLiteral listLiteral = langParser("[]").listLiteral();
		assertEquals(listLiteral.getElements().size(), 0);
		listLiteral = langParser("[101,a,'b']").listLiteral();
		assertEquals(listLiteral.getElements().size(), 3);
		assertEquals(listLiteral.getElements().get(0).getClass(), LongLiteral.class);
		assertEquals(((LongLiteral) listLiteral.getElements().get(0)).longValue(), 101L);
		assertEquals(listLiteral.getElements().get(1).getClass(), Identifier.class);
		assertEquals(((Identifier) listLiteral.getElements().get(1)).getName(), "a");
		assertEquals(listLiteral.getElements().get(2).getClass(), StringLiteral.class);
		assertEquals(((StringLiteral) listLiteral.getElements().get(2)).getValue(), "b");
	}

	/**
	 * Test a collection access operator
	 *
	 * @throws ParseException
	 */
	@Test
	public void testCollectionAccess() throws ParseException {
		Expression expression = langParser("a[3]").expression();
		assertTrue(expression instanceof CollectionAccess);
		CollectionAccess collectionAccess = (CollectionAccess) expression;
		assertTrue(collectionAccess.getCollection() instanceof Identifier);
		assertEquals(((Identifier) collectionAccess.getCollection()).getName(), "a");
		assertTrue(collectionAccess.getKey() instanceof LongLiteral);
		assertEquals(((LongLiteral) collectionAccess.getKey()).longValue(), 3L);
	}

	/**
	 * Test a collection access nested inside another collection access
	 *
	 * @throws ParseException
	 */
	@Test
	public void testNestedCollectionAccess() throws ParseException {
		Expression expression = langParser("x['g'][v]").expression();
		assertTrue(expression instanceof CollectionAccess);
		CollectionAccess collectionAccess = (CollectionAccess) expression;
		assertTrue(collectionAccess.getCollection() instanceof CollectionAccess);
		CollectionAccess collectionAccess1 = (CollectionAccess) collectionAccess.getCollection();
		assertEquals(((Identifier) collectionAccess1.getCollection()).getName(), "x");
		assertTrue(collectionAccess1.getKey() instanceof StringLiteral);
		assertEquals(((StringLiteral) collectionAccess1.getKey()).getValue(), "g");
		assertTrue(collectionAccess.getKey() instanceof Identifier);
		assertEquals(((Identifier) collectionAccess.getKey()).getName(), "v");
	}

	/**
	 * Test function literal
	 *
	 * @throws ParseException
	 */
	@Test
	public void testFunctionLiteral() throws ParseException {
		FunctionLiteral functionLiteral = langParser("fn(a,b) -> add(a,b)").functionLiteral();
		List<Identifier> parameters = functionLiteral.getParameters();
		assertEquals(parameters.size(), 2);
		assertEquals(parameters.get(0).getName(), "a");
		assertEquals(parameters.get(1).getName(), "b");
		Block body = functionLiteral.getBody();
		assertEquals(body.getExpressions().size(), 1);
		assertTrue(body.getExpressions().get(0) instanceof FunctionCall);
		FunctionCall functionCall = (FunctionCall) functionLiteral.getBody().getExpressions().get(0);
		assertTrue(functionCall.getFunctionValue() instanceof Identifier);
		assertEquals(((Identifier) functionCall.getFunctionValue()).getName(), "add");
		assertEquals(functionCall.getArguments().size(), 2);
		assertTrue(functionCall.getArguments().get(0) instanceof Identifier);
		assertEquals(((Identifier) functionCall.getArguments().get(0)).getName(), "a");
		assertEquals(((Identifier) functionCall.getArguments().get(1)).getName(), "b");
	}

	/**
	 * Test parsing a generic expression
	 *
	 * @throws ParseException
	 */
	@Test
	public void testExpression() throws ParseException {
		Expression expression = langParser("a").expression();
		assertEquals(expression.getClass(), Identifier.class);
		expression = langParser("a()").expression();
		assertEquals(expression.getClass(), FunctionCall.class);
		expression = langParser("a.b").expression();
		assertEquals(expression.getClass(), MemberAccess.class);
		expression = langParser("1").expression();
		assertEquals(expression.getClass(), LongLiteral.class);
		expression = langParser("1.1").expression();
		assertEquals(expression.getClass(), DoubleLiteral.class);
		expression = langParser("if(a,b,c)").expression();
		assertEquals(expression.getClass(), TernaryIf.class);
	}

	/**
	 * Test parsing a comma-delimited list of expressions
	 *
	 * @throws ParseException
	 */
	@Test
	public void testExpressionList() throws ParseException {
		List<Expression> list = langParser("").expressionList();
		assertEquals(list.size(), 0);
		list = langParser("a").expressionList();
		assertEquals(list.size(), 1);
		list = langParser("a, b").expressionList();
		assertEquals(list.size(), 2);
	}

	/**
	 * Test syntax error causing parse exception
	 *
	 * @throws ParseException
	 */
	@Test(expectedExceptions = ParseException.class)
	public void testUnbalancedELTag() throws ParseException {
		parser("a<%=b()").block();
	}

	/**
	 * Test parsing invalid token
	 *
	 * @throws ParseException
	 */
	@Test(expectedExceptions = TokenMgrError.class)
	public void testInvalidToken() throws ParseException {
		parser("<%=foo@bar%>").block();
	}

	/**
	 * Test parsing invalid number
	 *
	 * @throws ParseException
	 */
	@Test(expectedExceptions = ParseException.class)
	public void testInvalidNumber() throws ParseException {
		parser("<%=-41a%>").block();
	}

	/**
	 * Test incorrect syntax in accessing member of returned object
	 *
	 * @throws ParseException
	 */
	@Test(expectedExceptions = ParseException.class)
	public void testInvalidMemberAccess() throws ParseException {
		parser("<%=a()b%>").block();
	}

	/**
	 * Test missing argument in function call
	 *
	 * @throws ParseException
	 */
	@Test(expectedExceptions = ParseException.class)
	public void testInvalidFunctionCall() throws ParseException {
		parser("<%=f(a,)%>").block();
	}

	/**
	 * Test missing key in dictionary literal
	 *
	 * @throws ParseException
	 */
	@Test(expectedExceptions = ParseException.class)
	public void testDictionaryLiteralMissingKey() throws ParseException {
		parser("<%={:b}%>").block();
	}

	/**
	 * Test missing value in dictionary literal
	 *
	 * @throws ParseException
	 */
	@Test(expectedExceptions = ParseException.class)
	public void testDictionaryLiteralMissingValue() throws ParseException {
		parser("<%={a:}%>").block();
	}

	/**
	 * Test missing first entry in dictionary literal
	 *
	 * @throws ParseException
	 */
	@Test(expectedExceptions = ParseException.class)
	public void testDictionaryLiteralMissingFirstEntry() throws ParseException {
		parser("<%={,}%>").block();
	}

	/**
	 * Test missing second entry in dictionary literal
	 *
	 * @throws ParseException
	 */
	@Test(expectedExceptions = ParseException.class)
	public void testDictionaryLiteralMissingSecondEntry() throws ParseException {
		parser("<%={a:b,}%>").block();
	}

	/**
	 * Test missing first entry in list literal
	 *
	 * @throws ParseException
	 */
	@Test(expectedExceptions = ParseException.class)
	public void testListLiteralMissingFirstElement() throws ParseException {
		parser("<%=[,]%>").block();
	}

	/**
	 * Test missing second entry in list literal
	 *
	 * @throws ParseException
	 */
	@Test(expectedExceptions = ParseException.class)
	public void testListLiteralMissingSecondElement() throws ParseException {
		parser("<%=[a,]%>").block();
	}

	/**
	 * Returns a parser in expression language state
	 *
	 * @param value string to parse
	 * @return Parser instance with state set to expression language
	 */
	protected static Parser langParser(String value) {
		Parser parser = parser(value);
		parser.token_source.SwitchTo(Parser.EL);
		return parser;
	}

	/**
	 * Returns a parser
	 *
	 * @param value string to parser
	 * @return Parser instance
	 */
	protected static Parser parser(String value) {
		return new Parser(new StringReader(value));
	}

}

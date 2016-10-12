package cn.enilu.flash.core.util;

import org.junit.Test;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

public class CommandExecutorTest {

	@Test
	public void testNoArgs() throws Exception {
		CommandExecutor executor = new CommandExecutor("date");
		assertTrue(executor.execute());

		List<String> output = executor.getStdOutput();
		assertEquals(1, output.size());

		Calendar cal = Calendar.getInstance();
		String year = String.valueOf(cal.get(Calendar.YEAR));
		assertTrue(output.get(0).contains(year));
	}

	@Test
	public void testWithArgs() throws Exception {
		CommandExecutor executor = new CommandExecutor("echo");
		executor.addArg("abc");
		assertTrue(executor.execute());

		List<String> output = executor.getStdOutput();
		assertEquals(1, output.size());
		assertEquals("abc", output.get(0));
	}

	@Test
	public void testError() throws Exception {
		CommandExecutor executor = new CommandExecutor("ls");
		executor.setEnv("LANG", "C");
		String dir = "/not_exists" + System.currentTimeMillis();
		executor.addArg(dir);

		assertFalse(executor.execute());
		List<String> errors = executor.getErrorOutput();
		assertEquals(1, errors.size());
		assertTrue(errors.get(0).contains("No such file or directory"));
	}

	@Test
	public void testStdin() throws Exception {
		CommandExecutor executor = new CommandExecutor("cat");
		executor.setStdin("abc");
		assertTrue(executor.execute());

		List<String> output = executor.getStdOutput();
		assertEquals(1, output.size());
		assertEquals("abc", output.get(0));
	}
}

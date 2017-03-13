package com.austinv11.etf.benchmarks;

import com.austinv11.etf.ETFConfig;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

public class Benchmarks {
	
	@State(Scope.Benchmark)
	public static class Context {
		
		ETFConfig config;
		
		@Setup(Level.Trial)
		public void init() {
			config = new ETFConfig().setBert(false).setCompression(false)
					.setIncludeDistributionHeader(false).setIncludeHeader(false).setLoqui(true).setVersion(131);
		}
		
		@TearDown(Level.Trial)
		public void clean() {
			config = null;
		}
	}
	
	public static class TestClass {
		
	}
	
	@Benchmark
	@BenchmarkMode(Mode.All)
	@OutputTimeUnit(TimeUnit.NANOSECONDS)
	public void etf(Context context) {
		//TODO
	}
}

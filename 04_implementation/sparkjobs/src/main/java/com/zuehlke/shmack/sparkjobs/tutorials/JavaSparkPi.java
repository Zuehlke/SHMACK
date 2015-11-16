package com.zuehlke.shmack.sparkjobs.tutorials;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;

import com.zuehlke.shmack.sparkjobs.base.TestableSparkJob;

/**
 * Computes an approximation to pi Usage: JavaSparkPi [slices]
 */
public final class JavaSparkPi extends TestableSparkJob<Double> implements Serializable {

	private static final long serialVersionUID = 2597935909649303078L;
	
	public static void main(String[] args) throws Exception {
	    SparkConf sparkConf = new SparkConf().setAppName("JavaSparkPi");
	    JavaSparkContext jsc = new JavaSparkContext(sparkConf);
	    
	    JavaSparkPi javaSparkPi = new JavaSparkPi();
	    Double result = javaSparkPi.execute(jsc);

	    System.out.println("Pi is roughly " + result);

	    jsc.stop();
	  }
	
	
	@SuppressWarnings("serial")
	@Override
	public Double execute(JavaSparkContext spark) {
		int slices = 2;
		int n = 100000 * slices;
		List<Integer> l = new ArrayList<Integer>(n);
		for (int i = 0; i < n; i++) {
			l.add(i);
		}

		JavaRDD<Integer> dataSet = spark.parallelize(l, slices);

		int count = dataSet.map(new Function<Integer, Integer>() {
			@Override
			public Integer call(Integer integer) {
				double x = Math.random() * 2 - 1;
				double y = Math.random() * 2 - 1;
				return (x * x + y * y < 1) ? 1 : 0;
			}
		}).reduce(new Function2<Integer, Integer, Integer>() {
			@Override
			public Integer call(Integer integer, Integer integer2) {
				return integer + integer2;
			}
		});

		return 4.0 * count / n;
	}

	@Override
	public String getApplicationName() {
		return "JavaSparkPi";
	}
}

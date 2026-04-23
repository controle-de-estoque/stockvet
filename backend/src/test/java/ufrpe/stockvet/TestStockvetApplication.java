package ufrpe.stockvet;

import org.springframework.boot.SpringApplication;

public class TestStockvetApplication {

	public static void main(String[] args) {
		SpringApplication.from(StockvetApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

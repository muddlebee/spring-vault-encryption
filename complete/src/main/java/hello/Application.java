package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.vault.core.VaultSysOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.VaultTransitOperations;
import org.springframework.vault.support.VaultMount;

@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired
	private VaultTemplate vaultTemplate;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {

		// Let's encrypt some data using the Transit backend.
		VaultTransitOperations transitOperations = vaultTemplate.opsForTransit();

		// We need to setup transit first (assuming you didn't set up it yet).
		VaultSysOperations sysOperations = vaultTemplate.opsForSys();

		if (!sysOperations.getMounts().containsKey("transit/")) {

			sysOperations.mount("transit", VaultMount.create("transit"));

			transitOperations.createKey("foo-key");
		}

		// Encrypt a plain-text value
		String ciphertext = transitOperations.encrypt("foo-key", "Secure message");

		System.out.println("Encrypted value");
		System.out.println("-------------------------------");
		System.out.println(ciphertext);
		System.out.println("-------------------------------");
		System.out.println();

		// Decrypt

		String plaintext = transitOperations.decrypt("foo-key", ciphertext);

		System.out.println("Decrypted value");
		System.out.println("-------------------------------");
		System.out.println(plaintext);
		System.out.println("-------------------------------");
		System.out.println();
	}
}

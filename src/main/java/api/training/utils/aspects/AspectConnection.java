package api.training.utils.aspects;

import api.training.config.Config;
import api.training.exceptions.Exceptions;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

@Aspect
public class AspectConnection {

	@Pointcut("@annotation(HealthCheck)")
	public void checkConnection() {

	}

	@Before("checkConnection()")
	public void beforeCallMethodHealthCheckAnn() {
		if (!isConn()) {
			throw new Exceptions.ConnectionException();
		}
	}

	public boolean isConn() {
		try (Socket socket = new Socket()) {
			socket.connect(new InetSocketAddress(Config.getConfig().getHost(),
					Config.getConfig().getPort()), 1000);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
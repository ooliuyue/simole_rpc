package com.ooliuyue.simplRpc.rpc;

import com.ooliuyue.simplRpc.utils.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Auther: ly
 * @Date: 2019/3/13 17:53
 */

public class SimpleRpc {
    /**
     *将指定服务暴露出来，供客户端远程调用
     * @param service 需要暴露的服务
     * @param port  暴露的端口号
     * @throws Exception
     */
    public static void export(final Object service,int port) throws Exception{
        if (service == null)
            throw new IllegalArgumentException("service not null");
        if (port < 0 || port > 65535)
            throw new IllegalArgumentException("port in [0,65535]");
        System.out.println("Export Service:" + service.getClass().getName() + " port:" + port );
        /**
         *  Socket编程步骤
         *  服务器端创建ServerSocket对象，调用accept方法返回Socket对象
         *  客户端创建Socket对象，通过端口连接到服务器
         *  客户端、服务器端都使用Socket中的getInputStream方法和getOutputStream方法获得输入流和输出流，
         *  进一步进行数据读写操作
         */
        /**
         * socket在服务器端上的使用
         * 1.getInputStream方法得到的是一个输入流，服务端的Socket对象上的getInputStream方法得到的输入流其实就是从客户端发送给服务器端的数据流。
         * 2.getOutputStream方法得到的是一个输出流，服务端的Socket对象上的getOutputStream方法得到的输出流其实就是发送给客户端的数据。
         */
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            final Socket socket = serverSocket.accept();
            new Thread(() -> {
                ObjectInputStream input = null;
                ObjectOutputStream output = null;
                try {
                    try {
                        //将客户端发送给服务端的数据流反序列化成对象
                        input = new ObjectInputStream(socket.getInputStream());
                        String methodName = input.readUTF();
                        Class<?>[] paramTypes = (Class<?>[]) input.readObject();
                        Object[] arguments = (Object[]) input.readObject();

                        Method method = service.getClass().getMethod(methodName, paramTypes);
                        //反射调用服务实现，获取执行结果
                        Object result = method.invoke(service, arguments);

                        //将执行结果反序列化，然后发送给客户端
                        output = new ObjectOutputStream(socket.getOutputStream());
                        output.writeObject(result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (input != null)
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    if (output != null)
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }).start();
        }
    }

    /**
     * 获取远程服务
     * @param interfaceClass 服务接口class
     * @param host 远程IP地址
     * @param port 远程端口号
     * @param <T> 指定接口的实例
     * @return
     * @throws Exception
     */
    /**
     *
     * socket在客户端上的使用
     * 1.getInputStream方法可以得到一个输入流，客户端的Socket对象上的getInputStream方法得到输入流其实就是从服务器端返回的数据。
     * 2.getOutputStream方法得到的是一个输出流，客户端的Socket对象上的getOutputStream方法得到的输出流其实就是发送给服务器端的数据。
     */
    public static <T> T getRemoteService(final Class<T> interfaceClass,final String host,final int port)
            throws Exception {
        verifyGetRemoteService(interfaceClass,host,port);
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),new Class<?>[]{interfaceClass},(Object proxy, Method method, Object[] args) ->{
            Socket socket = new Socket(host,port);
            System.out.println("get remote service :" + interfaceClass.getName() + " from " + host + ":" + port);

            //将远程服务调用所需的接口类、方法名、参数列表等编码后发送给服务提供者
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeUTF(method.getName());
            output.writeObject(method.getParameterTypes());
            output.writeObject(args);

            //result就是从服务器返回的数据
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            Object result = input.readObject();
            return  result;
        });

    }

    private static <T> void verifyGetRemoteService(final Class<T> interfaceClass, final String host, final int port) {
        if (interfaceClass == null)
            throw new IllegalArgumentException("interfaceClass not null");
        if (!interfaceClass.isInterface())
            throw new IllegalArgumentException("interfaceClass not a interface");
        if (!StringUtils.isNotBlank(host))
            throw new IllegalArgumentException("host not blank");
        if (port < 0 || port > 65535)
            throw new IllegalArgumentException("port in [0,65535]");
    }

}

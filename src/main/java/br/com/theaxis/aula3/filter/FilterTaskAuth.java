package br.com.theaxis.aula3.filter;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.theaxis.aula3.user.IUserRepository;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            jakarta.servlet.FilterChain filterChain)
            throws java.io.IOException, jakarta.servlet.ServletException {
        // Lógica de autenticação para as rotas de tarefas
        // Exemplo: Verificar se o usuário está autenticado antes de permitir o acesso
        // às rotas de tarefas
        // Se o usuário não estiver autenticado, você pode retornar uma resposta de erro
        // ou redirecionar para a página de login
        var servLetPath = request.getServletPath();
        if (servLetPath.startsWith("/tasks")) {
            var authHeader = request.getHeader("Authorization");
            var authEncode = authHeader.substring("Basic".length()).trim();
            byte[] authDecoded = Base64.getDecoder().decode(authEncode);
            var authString = new String(authDecoded);
            String[] authArray = authString.split(":");
            String username = authArray[0];
            String password = authArray[1];
                // Prints de debug
                // System.out.println("Authorization");
                // System.out.println(authHeader);
                // System.out.println("authEncode");
                // System.out.println(authEncode);
                // System.out.println("authDecoded");
                // System.out.println(authDecoded);
                // System.out.println("authString");
                // System.out.println(authString);
                // System.out.println("username");
                // System.out.println(username);
                // System.out.println("password");
                // System.out.println(password);
                // Se o usuário estiver autenticado, continue com a cadeia de filtros
                // Validação de autenticação (exemplo simples, você pode implementar uma lógica
                // mais robusta)
            var user = this.userRepository.findByUsername(username);
            System.out.println("user");
            if (user == null) {
                response.sendError(401);
            } else {
                var passwordVerified = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordVerified.verified) {
                    request.setAttribute("idUser",user.getId());
                    System.out.println("user.getId()");
                    System.out.println(user.getId());
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(401);
                }
            }
        } else {
            filterChain.doFilter(request, response);
            return;
        }

    }
}

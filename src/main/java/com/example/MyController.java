package com.example;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

import com.example.model.ErrorBean;
import com.example.model.LoginUserModel;
import com.example.model.MessageDTO;
import com.example.model.MessagesModel;
import com.example.model.UserDTO;
import com.example.model.UsersModel;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import lombok.NoArgsConstructor;

/**
 * Jakarta MVCのコンロトーラクラスです。@Controllerアノテーションを付けましょう。
 * 
 * コントローラクラスはCDI beanであることが必須で、必ず@RequestScopedを付けます。
 * 
 * CDI beanには引数のないコンストラクタが必須なので、
 * Lombokの@NoArgsConstructorで空っぽのコンストラクタを作成します。
 * ただし、このクラスは宣言時に初期化してないfinalフィールドを持つため、
 * このままだとフィールドが初期化されない可能性があってコンパイルエラーとなります。
 * よって、force=true指定で該当フィールドを0かfalseかnullで初期化する処理を追加します。
 */
@Controller
@RequestScoped
@NoArgsConstructor(force = true)
@Path("/")
public class MyController {
	// uploaderRootで指定したディレクトリ直下に uploaded という名前のフォルダを作ること
	private final String uploaderRoot = "C:\\pleiades-ssj2023";
	private final String uploaderDirName = "uploaded";

	private final Models models;

	private final MessagesModel messagesModel;

	private final LoginUserModel loginUserModel;

	private final ErrorBean errorBean;

	private final UsersModel usersModel;

	@Inject
	public MyController(Models models, MessagesModel messagesModel, LoginUserModel loginUserModel,
			ErrorBean errorBean, UsersModel usersModel) {
		this.models = models;
		this.messagesModel = messagesModel;
		this.loginUserModel = loginUserModel;
		this.errorBean = errorBean;
		this.usersModel = usersModel;
	}

	/**
	 * @Path がないため、このメソッドはクラス全体が扱うURLのパスを扱います。
	 */
	@GET
	public String home() {
		models.put("appName", "メッセージアプリ");
		return "index.jsp";
	}

	@GET
	@Path("list")
	public String getMessage() {
		if (loginUserModel.getName() == null) {
			return "redirect:login";
		}
		models.put("uploaderDirName", uploaderDirName);

		return "list.jsp";
	}

	@POST
	@Path("list")
	public String postMessage(@BeanParam MessageDTO mes) {
		mes.setName(loginUserModel.getName());
		messagesModel.add(mes);
		// リダイレクトは "redirect:リダイレクト先のパス"
		return "redirect:list";
	}

	@POST
	@Path("fileupload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String postFileUpload(@FormParam("message") String message,
			@FormParam("uploadfile") EntityPart uploadFile) {
		var mes = new MessageDTO(loginUserModel.getName(), message, null);

		Optional<String> fileNameOpt = uploadFile.getFileName();
		var fileName = fileNameOpt.orElse("");
		// ファイルが選択されてないとき
		if(fileName.equals("")) {
			messagesModel.add(mes);
			return "redirect:list";
		}
		// ファイルが選択されているとき
		try (InputStream content = uploadFile.getContent()) {
			var utfFileName = new String(fileNameOpt.get().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
			Files.copy(content, java.nio.file.Path
					.of(uploaderRoot + File.separator + uploaderDirName + File.separator + utfFileName));
			mes.setFileName(utfFileName);
			messagesModel.add(mes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:list";
	}

	@GET
	@Path("clear")
	public String clearMessage() {
		messagesModel.clear();
		return "redirect:list";
	}

	@GET
	@Path("login")
	public String getLogin() {
		loginUserModel.setName(null);
		return "login.jsp";
	}

	@POST
	@Path("login")
	public String postLogin(@BeanParam UserDTO userDTO) {
		var name = userDTO.getName();
		if (usersModel.auth(name, userDTO.getPassword())) {
			loginUserModel.setName(name);
			return "redirect:list";
		}
		errorBean.setMessage("ユーザ名またはパスワードが異なります");
		return "redirect:login";
	}
}

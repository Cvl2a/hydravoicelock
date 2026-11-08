# HydraVoiceLock v0.1

## Como gerar o APK (sem Android Studio)
1. Crie um repositório no GitHub (pode ser privado).
2. Envie TODOS os arquivos desta pasta (inclusive a pasta oculta .github) pelo navegador do notebook: Add file > Upload files.
3. Aba Actions > "Build APK" > Run workflow (ou ele roda sozinho ao enviar).
4. Quando ficar verde, abra a execução e baixe o artefato "HydraVoiceLock-apk".
5. Instale o app-debug.apk no celular (permita "instalar apps desconhecidos").

## Configurar no celular (botões 1 a 5 do app, nessa ordem)
Microfone > Administrador > Exibir sobre outros apps > PIN > Iniciar escuta.

## Comandos (sempre comecam com "hydra")
- hydra trancar: tranca a tela
- hydra emergência: fecha o portão e tranca
- hydra abrir <nome do app>
- hydra liberar: abre tela de PIN; PIN certo libera o portão por 5 min

## Limites honestos desta versão
- NAO verifica que a voz e a sua (fase 2, precisa de modelo de voz). Por isso so TRANCAR funciona so com voz; liberar exige PIN.
- NAO destrava a tela do sistema (o Android nao permite a apps).
- NAO impede force stop nem desinstalacao por quem tem o celular desbloqueado.
- Nao foi compilado nem testado em aparelho real antes da entrega.
- O reconhecimento de voz do Android pode apitar a cada reinicio e gastar bateria.

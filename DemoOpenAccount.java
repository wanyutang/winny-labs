package com.demo;

/**
 * 開戶確認 SD 文件與程式註解規範 DEMO SDD—Software Design Document
 * 全流程註解與程式同步設計範例
 */
public class DemoOpenAccount {

    @Autowired
    AMLComp amlComp;

    @Autowired
    EmailComp emailComp;

    @Autowired
    DEMAPI demAPI;

    /**
     * 開戶流程主要步驟 SD 註解文件
     * <pre>
     * 1. 取得開戶主檔資料
     *     1-1: 取得主檔 DB資料
     *     1-2: 解密身分證字號
     * 2. AML 黑名單檢查
     *     2-1: 呼叫 AML 合規API
     * 3. 呼叫 DEMO379 電文API並根據回傳結果更新主檔
     *     3-1: 若回傳錯誤 -> 狀態設為 E，拋例外
     *     3-2: 若成功 -> 狀態設為 C，記錄開戶帳號
     *      - 開戶電文 DEMO379 電文API = demAPI.sendOpenAccount
     *      - 主檔 DB資料 = db.getAccountInfo
     * 4. FTP 上傳
     *     4-1: 上傳失敗僅記錄log
     * 5. 驗證 Email 認證結果
     * </pre>
     */
    public DemoOpenAccountRes openAcct() {
        // 1-1. 取得主檔 DB資料
        AccountInfo info = db.getAccountInfo(session.getCaseNo());

        // 1-2. 解密身分證字號
        info.setIdNo(EncodeUtil.base64Decode(info.getIdNo()));

        // 2-1. AML 黑名單合規API電文呼叫
        String amlCode = amlComp.check(info);

        // 3. 呼叫 DEMO379 電文API並根據回傳結果更新主檔
        try {
            Demo379Response demoRes = demAPI.sendOpenAccount(info, amlCode);
            if ("E".equals(demoRes.getStatus())) {
                // 3-1. 回傳錯誤，更新主檔狀態E並拋異常
                db.updateStatus(info.getId(), "E");
                throw new Exception("DEMO379 fail");
            } else {
                // 3-2. 成功，更新主檔帳號資訊
                db.updateAccountNo(info.getId(), demoRes.getAccountNo());
            }
        } catch (Exception e) {
            System.out.println("Error on DEMO379: " + e.getMessage());
        }

        // 4. FTP 上傳流程 - 失敗僅log
        try {
            ftpAPI.uploadSeal(info);
        } catch (Exception e) {
            System.out.println("Seal FTP error: " + e.getMessage());
        }

        // 5. Email 認證流程
        boolean mailValid = emailComp.validate(info.getMail());

        return DemoOpenAccountRes.builder()
            .mail(info.getMail())
            .emailVerified(mailValid)
            .build();
    }
}
# _plugins/backlink_converter.rb
module Jekyll
  class UnifiedMarkdownConverter < Jekyll::Converters::Markdown
    def convert(content)

      # 處理 `![[filename | width]]` 或 `![[filename]]` 特定格式
      content = content.gsub(/!\[\[(.+?)(?:\s*\|\s*(\d+))?\]\]/) do
        filename = Regexp.last_match(1).strip
        width = Regexp.last_match(2)
        if width
          "<img src='/assets/images/#{filename}' width='#{width}' alt='#{filename}' />"
        else
          "<img src='/assets/images/#{filename}' alt='#{filename}' />"
        end
      end

      # 替換 Markdown 鏈接中的 | 為全形符號 ｜，避免被誤認為表格分隔符
      content = content.gsub(/\[([^\]]+)\|([^\]]+)\]\(([^)]+)\)/) do
        "[#{$1}｜#{$2}](#{$3})" # 將 | 替換成 ｜（全形符號）
      end

      # 調用原始 Markdown 渲染邏輯處理其他內容
      super(content)
    end
  end
end

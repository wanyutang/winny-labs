# _plugins/shields_img_converter.rb

module Jekyll
  Jekyll::Hooks.register :site, :post_write do |site|
    Dir.glob("_site/**/*.html").each do |file|
      content = File.read(file)
      content.gsub!(/<a[^>]*href="(https:\/\/img\.shields\.io\/[^"]+)"[^>]*>\s*<img[^>]*src="(https:\/\/img\.shields\.io\/[^"]+)"[^>]*>\s*<\/a>/) do
        url = Regexp.last_match(2)
        "<img src='#{url}' alt='shields badge' loading='lazy'>"
      end
      File.write(file, content)
    end
  end
end

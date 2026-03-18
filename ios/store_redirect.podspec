Pod::Spec.new do |s|
  s.name             = 'store_redirect'
  s.version          = '2.0.2'
  s.summary          = 'Redirect users to an app page in Google Play Store and Apple App Store.'
  s.description      = <<-DESC
Redirect users to an app page in Google Play Store and Apple App Store.
                       DESC
  s.homepage         = 'https://github.com/Danesz/store_redirect'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Daniel Dallos' => 'flutter@danieldallos.com' }
  s.source           = { :path => '.' }
  s.source_files     = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  s.platform = :ios, '12.0'
  s.swift_version = '5.0'
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES' }
end

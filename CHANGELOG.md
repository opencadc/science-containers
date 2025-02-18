# Changelog

## [0.6.0](https://github.com/opencadc/science-containers/compare/v0.5.0...v0.6.0) (2025-02-18)


### Features

* **build:** added gradle build file ([e2944d3](https://github.com/opencadc/science-containers/commit/e2944d3b37f2b629c0503b78a400985c4122bc96))
* **cadc-sso:** added base implementation of the adapter ([d0fdc5c](https://github.com/opencadc/science-containers/commit/d0fdc5cc7256e83d9efe5e6f0b9acf7cc89a4c29))
* **cadc-sso:** fixed firefly runtime issues and added documentation ([e50ef6c](https://github.com/opencadc/science-containers/commit/e50ef6c263b2e4e0f0355fce7ee9c41cff63fc72))
* **entrypoint:** updated entrypoint logic ([e91431a](https://github.com/opencadc/science-containers/commit/e91431a650313b35f91b39c85b864fd85dd8093e))
* **k8s:** added manifest to deploy firefly on keel ([b42ce76](https://github.com/opencadc/science-containers/commit/b42ce766caa4e0bcfab258a30c19ad2f61752ff2))
* **prject:** setup for cadc-sso project ([9f22a34](https://github.com/opencadc/science-containers/commit/9f22a3408649229beb2f8d1a59197b806b31293d))


### Bug Fixes

* **build:** removed unused configuration section from build.gradle ([4b8fb81](https://github.com/opencadc/science-containers/commit/4b8fb81911b3c16ad7f32ee389886100c67651b1))
* **cadc-sso:** added lib to be included ([dfacbdb](https://github.com/opencadc/science-containers/commit/dfacbdbfeeaa71c65f343a5254ea465f5d23503d))
* **cadc-sso:** added significantly more testing and method to check if headers need to be added for downstream api access ([153a8a7](https://github.com/opencadc/science-containers/commit/153a8a7c5ca01ed466fb9b2dbe20fa5a71d02981))
* **docker:** removed reference to entrypoint.py ([cb686b8](https://github.com/opencadc/science-containers/commit/cb686b81454514808dc5c38d05b15be6d1622d1f))
* **docs:** updated based pr-comments ([070d81e](https://github.com/opencadc/science-containers/commit/070d81e20363c2ae026fd429aab04368f3c9b0d9))
* **entrypoint:** fixes for firefly entrypoint ([ee33764](https://github.com/opencadc/science-containers/commit/ee337643f99cd35a081b00676d3a2a8b368a2d74))
* **gitignore:** added cadc-sso/lib folder to be tracked by git ([985bee1](https://github.com/opencadc/science-containers/commit/985bee1bb3afb933c6adf3016c8f98b4c0970155))
* **gitignore:** fixes ([42ce27a](https://github.com/opencadc/science-containers/commit/42ce27a69137cc97c1a5aa26d004a8bf6b2dbea9))
* **gradle:** fixed gradle launcher vs daemon vm issues ([d11fc47](https://github.com/opencadc/science-containers/commit/d11fc478d4deddea60043072d591caa2b1e031a5))
* remove image link ([bb627a6](https://github.com/opencadc/science-containers/commit/bb627a61834fae97ab5ee010579b490a763bf423))


### Documentation

* small text chagne ([2720ea5](https://github.com/opencadc/science-containers/commit/2720ea517c1ada920470f5aa95ffcf0221871311))
* update for new registry authentication ([ea6b464](https://github.com/opencadc/science-containers/commit/ea6b4640732af037f7510ac7727893fff96e2a33))

## [0.5.0](https://github.com/opencadc/science-containers/compare/v0.4.2...v0.5.0) (2024-06-07)


### Features

* **base Image:** debugged micromamba installation process ([4a6b0ac](https://github.com/opencadc/science-containers/commit/4a6b0acc470da7ffc265459d4de443464ecbb607))
* **ci:** added pre-commit ci action ([2cc8b3d](https://github.com/opencadc/science-containers/commit/2cc8b3d92aeca39e3605a38d2fe3e90fbe36c347))


### Documentation

* **rework:** index rework ([0072015](https://github.com/opencadc/science-containers/commit/00720150466de4de4d2097a405f839e0ff48d764))
* **rework:** rework ([5bc217c](https://github.com/opencadc/science-containers/commit/5bc217cc11b4ab72b156d8f96f8d35995b4a8d2b))

## [0.5.0](https://github.com/opencadc/science-containers/compare/v0.4.2...v0.5.0) (2024-06-07)


### Features

* **base Image:** debugged micromamba installation process ([4a6b0ac](https://github.com/opencadc/science-containers/commit/4a6b0acc470da7ffc265459d4de443464ecbb607))
* **ci:** added pre-commit ci action ([2cc8b3d](https://github.com/opencadc/science-containers/commit/2cc8b3d92aeca39e3605a38d2fe3e90fbe36c347))


### Documentation

* **rework:** index rework ([0072015](https://github.com/opencadc/science-containers/commit/00720150466de4de4d2097a405f839e0ff48d764))
* **rework:** rework ([5bc217c](https://github.com/opencadc/science-containers/commit/5bc217cc11b4ab72b156d8f96f8d35995b4a8d2b))

## [0.4.2](https://github.com/opencadc/scicon/compare/v0.4.1...v0.4.2) (2023-10-25)


### Bug Fixes

* **docs:** fixed the spelling of ubuntu ([ff272b3](https://github.com/opencadc/scicon/commit/ff272b3d0033a970d7d8dce8b4d3417732953edc))

## [0.4.1](https://github.com/opencadc/scicon/compare/v0.4.0...v0.4.1) (2023-10-24)


### Bug Fixes

* **build:** fixed ref issue in docker build ([d971218](https://github.com/opencadc/scicon/commit/d971218673fd62ff139db6fd7fc2fa1aeea138f8))

## [0.4.0](https://github.com/opencadc/scicon/compare/v0.3.0...v0.4.0) (2023-10-24)


### Features

* **build:** add quotes around python versions ([a64ef9b](https://github.com/opencadc/scicon/commit/a64ef9be5945c9cba1e0634317aa6503d2e06392))

## [0.3.0](https://github.com/opencadc/scicon/compare/v0.2.2...v0.3.0) (2023-10-24)


### Features

* **Github-Actions:** added deloyment action for base container ([32df4a2](https://github.com/opencadc/scicon/commit/32df4a2cb4183ba57996524f2735f45e5c9d9b20))


### Documentation

* **docs:** root README.md ([68ea7d3](https://github.com/opencadc/scicon/commit/68ea7d3a20e80dac9ffe0c689b7e4e11a2083dff))
* **docs:** root README.md ([c31227c](https://github.com/opencadc/scicon/commit/c31227c998cafbd7f4fe1af103989ccc3c1abe20))

## [0.2.2](https://github.com/opencadc/scicon/compare/v0.2.1...v0.2.2) (2023-10-24)


### Bug Fixes

* **Github-Actions:** working on deployment chain ([c14f04d](https://github.com/opencadc/scicon/commit/c14f04dbf0a46db26821f2aa9134c9b438665ae8))

## [0.2.1](https://github.com/opencadc/scicon/compare/v0.2.0...v0.2.1) (2023-10-24)


### Bug Fixes

* **Github-Actions:** working on deployment ([d19c775](https://github.com/opencadc/scicon/commit/d19c775de05eeb51931e68f72930a9b89c441303))

## [0.2.0](https://github.com/opencadc/scicon/compare/v0.1.0...v0.2.0) (2023-10-24)


### Features

* **Github-Actions:** added a new deployment step with matrix capabilties ([1d2b711](https://github.com/opencadc/scicon/commit/1d2b71185d9e757a7fc7588c2e375704514d1522))


### Documentation

* **docs:** initial port from science-containers ([af7abaf](https://github.com/opencadc/scicon/commit/af7abaf9276c51b2b6e6a748f0ffc9a179202932))

## 0.1.0 (2023-10-24)


### Features

* **docs:** added mkdocs ([8505ae8](https://github.com/opencadc/scicon/commit/8505ae80f38eefda829eba3b1de201326f80d095))
* **setup:** added python setup config ([e740204](https://github.com/opencadc/scicon/commit/e740204bef364144902201fbeb8ed6431536c7e3))


### Bug Fixes

* **CI/CD:** fixed issues with docs build ([1661518](https://github.com/opencadc/scicon/commit/1661518052492836200733459ed2aa4230024103))
* **Github-Actions:** fixed github actions syntax ([beafcc6](https://github.com/opencadc/scicon/commit/beafcc65d8103fe09375a6206845b80549319935))

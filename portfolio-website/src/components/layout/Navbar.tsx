import React, { useState, useEffect } from 'react';

export const Navbar: React.FC = () => {
    const [isScrolled, setIsScrolled] = useState(false);
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
    const [activeSection, setActiveSection] = useState('');

    const navLinks = [
        { name: 'About', href: '#about' },
        { name: 'Skills', href: '#skills' },
        { name: 'Projects', href: '#projects' },
        { name: 'Contact', href: '#contact' },
    ];

    useEffect(() => {
        const handleScroll = () => {
            setIsScrolled(window.scrollY > 50);
        };

        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    useEffect(() => {
        const handleScrollSpy = () => {
            const sections = navLinks.map(link => link.href.substring(1));
            const scrollPosition = window.scrollY + 100;

            for (const section of sections) {
                const element = document.getElementById(section);
                if (element) {
                    const { offsetTop, offsetHeight } = element;
                    if (scrollPosition >= offsetTop && scrollPosition < offsetTop + offsetHeight) {
                        setActiveSection(section);
                        break;
                    }
                }
            }
        };

        window.addEventListener('scroll', handleScrollSpy);
        return () => window.removeEventListener('scroll', handleScrollSpy);
    }, [navLinks]);

    const scrollToSection = (href: string) => {
        setIsMobileMenuOpen(false);
        const element = document.querySelector(href);
        if (element) {
            element.scrollIntoView({ behavior: 'smooth' });
        }
    };

    return (
        <nav
            className={`fixed top-0 left-0 right-0 z-50 transition-all duration-300 ${isScrolled
                ? 'bg-white/90 backdrop-blur-md shadow-md'
                : 'bg-transparent'
                }`}
        >
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex items-center justify-between h-16">
                    <a
                        href="#"
                        className="text-2xl font-bold text-gray-900 hover:text-primary-600 transition-colors"
                    >
                        Portfolio
                    </a>

                    {/* Desktop Navigation */}
                    <div className="hidden md:flex items-center space-x-8">
                        {navLinks.map((link) => (
                            <a
                                key={link.name}
                                href={link.href}
                                onClick={(e) => {
                                    e.preventDefault();
                                    scrollToSection(link.href);
                                }}
                                className={`font-medium transition-colors hover:text-primary-600 ${activeSection === link.href.substring(1)
                                    ? 'text-primary-600'
                                    : 'text-gray-700'
                                    }`}
                            >
                                {link.name}
                            </a>
                        ))}
                    </div>

                    {/* Mobile Menu Button */}
                    <button
                        className="md:hidden p-2 rounded-lg text-gray-700 hover:bg-gray-100"
                        onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                        aria-label="Toggle menu"
                    >
                        <svg
                            className="w-6 h-6"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                        >
                            {isMobileMenuOpen ? (
                                <path
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    strokeWidth={2}
                                    d="M6 18L18 6M6 6l12 12"
                                />
                            ) : (
                                <path
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    strokeWidth={2}
                                    d="M4 6h16M4 12h16M4 18h16"
                                />
                            )}
                        </svg>
                    </button>
                </div>

                {/* Mobile Menu */}
                {isMobileMenuOpen && (
                    <div className="md:hidden mt-4 pb-4 border-t border-gray-200">
                        <div className="flex flex-col space-y-2 pt-4">
                            {navLinks.map((link) => (
                                <a
                                    key={link.name}
                                    href={link.href}
                                    onClick={(e) => {
                                        e.preventDefault();
                                        scrollToSection(link.href);
                                    }}
                                    className={`font-medium py-2 transition-colors hover:text-primary-600 ${activeSection === link.href.substring(1)
                                        ? 'text-primary-600'
                                        : 'text-gray-700'
                                        }`}
                                >
                                    {link.name}
                                </a>
                            ))}
                        </div>
                    </div>
                )}
            </div>
        </nav>
    );
};
